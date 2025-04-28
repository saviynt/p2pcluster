package com.saviynt.p2pcluster.service;

import com.saviynt.p2pcluster.Constants;
import com.saviynt.p2pcluster.component.RemoteMessageListener;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteMessaging;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static com.saviynt.p2pcluster.Constants.NODE_TYPE;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagingService implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final transient Ignite ignite;

    private final RemoteMessageListener remoteMessageListener;

    @Qualifier("p2pMessageLocalListenerExecutor")
    private final transient Executor executor;

    @PostConstruct
    public void registerLocalMessageListener() {
        final IgniteMessaging messaging = ignite.message();

        messaging.localListen(Constants.TOPIC.LOCAL_LISTENER, (nodeId, msg) -> {
            CompletableFuture.supplyAsync(() -> {
                handleMessage(NODE_TYPE.LOCAL, nodeId, msg);
                return null;
            }, executor).whenComplete((o, throwable) -> {
                if (throwable != null) {
                    log.error("Error while processing message", throwable);
                }
            });
            return true;
        });
        log.info("Local listener registered on Ignite Node ID: {}, with POD IP Addr: {}",
                ignite.cluster().localNode().id(),
                System.getenv("POD_IP_ADDRESS"));
    }

    @PostConstruct
    public void registerRemoteMessageListener() {

        final IgniteMessaging messaging = ignite.message();

        final UUID remoteListen = messaging.remoteListen(Constants.TOPIC.REMOTE_LISTENER, remoteMessageListener);


        /*final UUID remoteListen = messaging.remoteListen(Constants.TOPIC.REMOTE_LISTENER, (nodeId, msg) -> {
            CompletableFuture.supplyAsync(() -> {
                handleMessage(NODE_TYPE.REMOTE, nodeId, msg);
                return null;
            }).whenComplete((o, throwable) -> {
                if (throwable != null) {
                    log.error("Error while processing message", throwable);
                }
            });
            return true;
        });*/
        log.info("Remote listener registered with Listener ID {} on Ignite ID: {} with POD IP Addr: {}.",
                remoteListen,
                ignite.cluster().localNode().id(),
                System.getenv("POD_IP_ADDRESS"));
    }

    private void handleMessage(NODE_TYPE nodeType, UUID nodeId, Object msg) {

        log.info("Received at Node: {}, with POD IP Addr: {}, Node type: {}, Node ID: {}, Msg: {}.",
                ignite.cluster().localNode().id(),
                System.getenv("POD_IP_ADDRESS"),
                nodeType,
                nodeId,
                msg);
    }

}
