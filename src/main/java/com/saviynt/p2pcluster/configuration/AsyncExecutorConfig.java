package com.saviynt.p2pcluster.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncExecutorConfig {

    @Bean(name = "p2pMessageLocalListenerExecutor")
    @Primary
    public Executor localTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4); // base number of threads
        executor.setMaxPoolSize(8); // max threads
        executor.setQueueCapacity(100); // how many tasks can queue up
        executor.setThreadNamePrefix("p2p-async-msg-local-lstnr");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }

    @Bean(name = "p2pMessageRemoteListenerExecutor")
    public Executor remoteTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4); // base number of threads
        executor.setMaxPoolSize(8); // max threads
        executor.setQueueCapacity(100); // how many tasks can queue up
        executor.setThreadNamePrefix("p2p-async-msg-remote-lstnr");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
