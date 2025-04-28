package com.saviynt.p2pcluster.component;


import com.saviynt.p2pcluster.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.saviynt.p2pcluster.Constants.NODE_TYPE;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoteMessageListener implements IgniteBiPredicate<UUID, Object> {

    //private final MessageHandlerService messageHandlerService;

    /*@Qualifier("p2pMessageRemoteListenerExecutor")
    private final transient Executor executor;*/

    @Override
    public boolean apply(UUID nodeId, Object msg) {
        if (msg instanceof MessageDto messageDto) {
            CompletableFuture.supplyAsync(() -> {
                handleMessage(NODE_TYPE.REMOTE, nodeId, messageDto);
                return null;
            }).whenComplete((o, throwable) -> {
                if (throwable != null) {
                    log.error("Error while processing message", throwable);
                }
            });
        }
        return true;
    }

    public void handleMessage(NODE_TYPE nodeType, UUID nodeId, MessageDto messageDto) {
        log.info("Received at POD IP Addr: {}, Node type: {}, from Node ID: {}, Msg: {}.",
                System.getenv("POD_IP_ADDRESS"),
                nodeType,
                nodeId,
                messageDto);
    }

}
