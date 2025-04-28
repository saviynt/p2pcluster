package com.saviynt.p2pcluster;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class P2pclusterApplication {

    public static void main(String[] args) {
        log.info("JAVA_TOOL_OPTIONS = {} ", System.getenv("JAVA_TOOL_OPTIONS"));
        SpringApplication.run(P2pclusterApplication.class, args);
    }

}
