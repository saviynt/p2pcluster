package com.saviynt.p2pcluster.configuration;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.DataPageEvictionMode;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.kubernetes.configuration.KubernetesConnectionConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.kubernetes.TcpDiscoveryKubernetesIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class IgniteConfig {

    @Value("${app.k8s.pod-name}")
    private String podName;

    @Bean
    public Ignite ignite(IgniteConfiguration cfg) {
        return Ignition.start(cfg);
    }

    @Bean
    @Profile("test")
    public IgniteConfiguration igniteTestConfiguration() {
        IgniteConfiguration cfg = new IgniteConfiguration();
        //cfg.setClientMode(true);
        //cfg.setIgniteInstanceName("ignite-node");
        cfg.setPeerClassLoadingEnabled(false);

        // Disable discovery
        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
        discoverySpi.setIpFinder(new TcpDiscoveryVmIpFinder(true));
        /*TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
        discoverySpi.setIpFinder(ipFinder);*/
        cfg.setDiscoverySpi(discoverySpi);
        //cfg.setFailureDetectionTimeout(5000); // 5 seconds
        return cfg;
    }

    @Bean
    @Profile("!test")
    public IgniteConfiguration igniteConfiguration() {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setIgniteInstanceName(podName);
        // Pod hostname, which is unique and stable in StatefulSets.
        // Kubernetes downward API recommended
        // cfg.setConsistentId(System.getenv("POD_NAME"));
        cfg.setPeerClassLoadingEnabled(false);
        cfg.setDiscoverySpi(getTcpDiscoverySpi());
        cfg.setDataStorageConfiguration(getDataStorageConfiguration());
        return cfg;
    }

    private static TcpDiscoverySpi getTcpDiscoverySpi() {
        KubernetesConnectionConfiguration kubeCfg = new KubernetesConnectionConfiguration();
        kubeCfg.setNamespace("p2p-ns");
        kubeCfg.setServiceName("p2p-service");

        // Configure TCP discovery SPI with Kubernetes IP finder
        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
        TcpDiscoveryKubernetesIpFinder ipFinder = new TcpDiscoveryKubernetesIpFinder(kubeCfg);

        discoverySpi.setIpFinder(ipFinder);
        return discoverySpi;
    }


    private static DataStorageConfiguration getDataStorageConfiguration() {
        DataRegionConfiguration drc = new DataRegionConfiguration();
        drc.setName ("default-data-region");
        drc.setInitialSize(10L * 1024 * 1024); //10MB
        drc.setMaxSize(40L * 1024 * 1024); //40MB
        drc.setPageEvictionMode(DataPageEvictionMode.RANDOM_2_LRU);
        drc.setPersistenceEnabled(false); // Data is stored only in memory, All data is lost on shutdown

        DataStorageConfiguration dsc = new DataStorageConfiguration();
        dsc.setDefaultDataRegionConfiguration(drc);

        return dsc;
    }


}
