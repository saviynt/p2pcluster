/*
package com.saviynt.p2pcluster.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.saviynt.p2pcluster.Constants.NODE_TYPE;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageHandlerService {

    private final Ignite ignite;

    public void handleMessage(NODE_TYPE nodeType, UUID nodeId, Object msg) {
        log.info("Received at Node: {}, with POD IP Addr: {}, Node type: {}, Node IgniteD: {}, Msg: {}.",
                ignite.cluster().localNode().id(),
                System.getenv("POD_IP_ADDRESS"),
                nodeType,
                nodeId,
                msg);

    }
}
*/
