package com.saviynt.p2pcluster.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.events.DiscoveryEvent;
import org.apache.ignite.events.EventType;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventingService {

    private final Ignite ignite;

    @PostConstruct
    public void registerEventListeners() {
        ignite.events().localListen(event -> {
                    log.info("[{}] Event occurred - Type: {}, Message: '{}', Timestamp: {}, Node ID: {}",
                            event.getClass().getSimpleName(),
                            event.name(),
                            event.message(),
                            Instant.ofEpochMilli(event.timestamp()),
                            ((DiscoveryEvent) event).eventNode().id());
                    return true;
                },
                EventType.EVT_NODE_JOINED,
                EventType.EVT_NODE_LEFT,
                EventType.EVT_NODE_FAILED);
    }
}
