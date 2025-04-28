package com.saviynt.p2pcluster.controller;


import com.saviynt.p2pcluster.Constants;
import com.saviynt.p2pcluster.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sample")
@RequiredArgsConstructor
public class CacheController {

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

    @PostMapping("/{key}/{value}")
    public ResponseEntity<ResponseMessage> put(@PathVariable String key, @PathVariable String value) {
        final IgniteCache<String, String> sampleCache = ignite.cache(Constants.SAMPLE_CACHE);
        sampleCache.put(key, value);

        return ResponseEntity.ok(new ResponseMessage(nodeName, nodeIpAddress, podNameSpace, podName, podUid,
                podIpAddress, "key: " + key + ", value: " + value));
    }

    @GetMapping("/{key}")
    public ResponseEntity<ResponseMessage> get(@PathVariable String key) {
        final IgniteCache<String, String> sampleCache = ignite.cache(Constants.SAMPLE_CACHE);

        return ResponseEntity.ok(new ResponseMessage(nodeName, nodeIpAddress, podNameSpace, podName, podUid,
                podIpAddress, sampleCache.get(key)));
        /*return ResponseEntity.ok(new ResponseMessage(nodeName, nodeIpAddress, podNameSpace, podName, podUid,
                podIpAddress, "some fixed value"));*/
    }
}
