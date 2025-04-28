package com.saviynt.p2pcluster;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
@Slf4j
class P2pclusterApplicationTests {

    @Autowired
    private Ignite ignite;

    @Test
    void contextLoads() {

    }

    /*@Test
    void testIgniteIsRunning() {
        assertNotNull(ignite);
        log.info("Ignite Node Name: {}", ignite.name());
    }*/

    @AfterAll
    static void tearDown() {
        Ignition.stopAll(true);
    }
}
