package com.saviynt.p2pcluster.controller;

import com.saviynt.p2pcluster.Constants;
import com.saviynt.p2pcluster.MessageDto;
import com.saviynt.p2pcluster.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final Ignite ignite;

    @Value("${app.k8s.node-name}")
    private String nodeName;

    @Value("${app.k8s.node-ip-address}")
    private String nodeIpAddress;

    @Value("${app.k8s.pod-name-space}")
    private String podNameSpace;

    @Value("${app.k8s.pod-name}")
    private String podName;

    @Value("${app.k8s.pod-uid}")
    private String podUid;

    @Value("${app.k8s.pod-ip-address}")
    private String podIpAddress;

    @PostMapping("/broadcast_to_others")
    public ResponseEntity<ResponseMessage> broadcastMessageToOtherPeers(@RequestBody String msg) {
        final IgniteMessaging messaging = ignite.message(ignite.cluster().forRemotes());
        messaging.send(Constants.TOPIC.LOCAL_LISTENER, msg);
        return ResponseEntity.ok(new ResponseMessage(nodeName, nodeIpAddress, podNameSpace, podName, podUid,
                podIpAddress, "msg: " + msg));
    }

    @PostMapping("/listen_me_from_remote")
    public ResponseEntity<ResponseMessage> broadcastMessage(@RequestBody String msg) {
        final IgniteMessaging messaging = ignite.message(ignite.cluster().forRemotes());
        final MessageDto messageDto = new MessageDto(msg, podIpAddress);
        messaging.send(Constants.TOPIC.REMOTE_LISTENER, messageDto);
        return ResponseEntity.ok(new ResponseMessage(nodeName, nodeIpAddress, podNameSpace, podName, podUid,
                podIpAddress, messageDto.getMsg()));
    }

}
