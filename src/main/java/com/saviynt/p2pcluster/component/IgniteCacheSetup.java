package com.saviynt.p2pcluster.component;

import com.saviynt.p2pcluster.Constants;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.ignite.Ignite;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IgniteCacheSetup {

    private final Ignite ignite;


    @PostConstruct
    public void init() {
        // Setting up SampleCache
        CacheConfiguration<String, String> cacheCfg = new CacheConfiguration<>();
        cacheCfg.setName(Constants.SAMPLE_CACHE);
        cacheCfg.setOnheapCacheEnabled(false);
        cacheCfg.setBackups(1);
        cacheCfg.setCacheMode(CacheMode.REPLICATED);
        cacheCfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
        ignite.getOrCreateCache(cacheCfg); // Safe for idempotent startup
    }
}
