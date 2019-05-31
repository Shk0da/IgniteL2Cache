package com.github.shk0da.demo.config;

import com.github.shk0da.demo.domain.Contact;
import com.github.shk0da.demo.domain.ContactGroup;
import com.github.shk0da.demo.domain.HistoryAudit;
import com.github.shk0da.demo.util.ignite.TcpDiscoveryKubernetesIpFinder;
import com.google.common.collect.Lists;
import com.sun.management.OperatingSystemMXBean;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.configuration.AddressResolver;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.events.EventType;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.hibernate.cache.spi.QueryResultsCache;
import org.hibernate.cache.spi.TimestampsCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.lang.System.setProperty;
import static org.apache.ignite.configuration.DataStorageConfiguration.DFLT_DATA_REGION_INITIAL_SIZE;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

@Slf4j
@Configuration
public class IgniteContextConfiguration {

    static {
        setProperty("java.net.preferIPv4Stack", "true");
    }

    @Bean(destroyMethod = "close")
    public Ignite igniteInstance(IgniteConfiguration igniteConfiguration) throws IgniteException {
        Ignite ignite = Ignition.start(igniteConfiguration);
        ignite.cluster().active(true);
        ignite.cluster().setBaselineTopology(ignite.cluster().forServers().nodes());
        log.debug("Ignite Cluster Nodes [{}]:", ignite.cluster().nodes().size());
        ignite.cluster().nodes().forEach(clusterNode -> log.debug("{}", clusterNode));
        return ignite;
    }

    @Bean
    public IgniteConfiguration igniteConfiguration(@Value("${spring.application.name}") String applicationName,
                                                   TcpDiscoverySpi tcpDiscoverySpi,
                                                   TcpCommunicationSpi tcpCommunicationSpi,
                                                   DataStorageConfiguration dataStorageConfiguration) {
        // Main
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        String nodeName = applicationName + " DataNode[" + UUID.randomUUID().toString() + "]";
        igniteConfiguration.setIgniteInstanceName(nodeName);
        System.setProperty("spring.jpa.properties.org.apache.ignite.hibernate.ignite_instance_name", nodeName);
        igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);
        igniteConfiguration.setCommunicationSpi(tcpCommunicationSpi);
        igniteConfiguration.setDataStorageConfiguration(dataStorageConfiguration);
        igniteConfiguration.setIncludeEventTypes(EventType.EVTS_DISCOVERY);

        // Caches
        CacheConfiguration<Object, Object> atomicCache = new CacheConfiguration<>();
        atomicCache.setCacheMode(CacheMode.PARTITIONED);
        atomicCache.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        atomicCache.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC);

        CacheConfiguration<Object, Object> transactionalCache = new CacheConfiguration<>();
        transactionalCache.setCacheMode(CacheMode.PARTITIONED);
        transactionalCache.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
        transactionalCache.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC);

        CacheConfiguration updateTimestampsCache = new CacheConfiguration<>(atomicCache);
        updateTimestampsCache.setName(TimestampsCache.class.getCanonicalName());

        CacheConfiguration standardQueryCache = new CacheConfiguration<>(atomicCache);
        standardQueryCache.setName(QueryResultsCache.class.getCanonicalName());

        CacheConfiguration contactCache = new CacheConfiguration<>(transactionalCache);
        contactCache.setName(Contact.class.getCanonicalName());

        CacheConfiguration contactGroupCache = new CacheConfiguration<>(transactionalCache);
        contactGroupCache.setName(ContactGroup.class.getCanonicalName());

        CacheConfiguration historyAuditCache = new CacheConfiguration<>(transactionalCache);
        historyAuditCache.setName(HistoryAudit.class.getCanonicalName());

        igniteConfiguration.setCacheConfiguration(
                updateTimestampsCache,
                standardQueryCache,
                contactCache,
                contactGroupCache,
                historyAuditCache
        );

        return igniteConfiguration;
    }

    @Data
    @ConfigurationProperties("ignite.discovery")
    public static class DiscoveryProperties {
        private long socketTimeout = TimeUnit.SECONDS.toMillis(20);
        private long ackTimeout = TimeUnit.SECONDS.toMillis(20);
        private long maxAckTimeout = TimeUnit.SECONDS.toMillis(60);
        private long networkTimeout = TimeUnit.SECONDS.toMillis(30);
        private int localPort = findAvailableTcpPort(47500, 48000);
        private int localPortRange = 1;
        private int reconnectCount = 10_000;
        private int maxMissedClientHeartbeats = 20;
        private List<String> nodes = Lists.newArrayList();
    }

    @Data
    @ConfigurationProperties("ignite.communication")
    public static class CommunicationProperties {
        private long connectTimeout = TimeUnit.SECONDS.toMillis(20);
        private long maxConnectTimeout = TimeUnit.SECONDS.toMillis(60);
        private int sharedMemoryPort = findAvailableTcpPort(48100, 48200);
        private int localPort = findAvailableTcpPort(47100, 47500);
        private int localPortRange = 1;
        private int reconnectCount = 10_000;
    }

    @Data
    @ConfigurationProperties("ignite.datastorage")
    public static class DataStorageProperties {
        private boolean persistenceEnabled = false;
        private long memoryOffHeapMaxMb = 2048;
        private String storagePath = "/var/service/name/storage";
        private String walPath = "/var/service/name/db/wal";
        private String walArchivePath = "/var/service/name/db/wal/archive";
    }

    @Data
    @ConfigurationProperties("ignite.k8s")
    public static class KubernetesProperties {
        private boolean shared = true;
        private String accountToken = "/etc/service/name/accessToken";
        private String masterUrl = "http://localhost.ru:8080";
        private String namespace = "demo-app-test";
        private String serviceName = "demo";
    }

    @Configuration
    @EnableConfigurationProperties({
            DiscoveryProperties.class, CommunicationProperties.class, DataStorageProperties.class, KubernetesProperties.class
    })
    public static class NetworkConfiguration {

        private static final double DATA_REGION_PERCENT = 0.80;

        private final DiscoveryProperties discoveryProps;
        private final CommunicationProperties communicationProps;
        private final DataStorageProperties dataStorageProperties;
        private final KubernetesProperties kubernetesProperties;

        public NetworkConfiguration(DiscoveryProperties discoveryProps,
                                    CommunicationProperties communicationProps,
                                    DataStorageProperties dataStorageProperties,
                                    KubernetesProperties kubernetesProperties) {
            this.discoveryProps = discoveryProps;
            this.communicationProps = communicationProps;
            this.dataStorageProperties = dataStorageProperties;
            this.kubernetesProperties = kubernetesProperties;
        }

        private static long calculateNonHeapMemoryForDataRegion(long offHeapDataRegionMb) {
            long calculatedSizeForDataRegion = Math.max(offHeapDataRegionMb * 1024L * 1024L, DFLT_DATA_REGION_INITIAL_SIZE);
            try {
                MemoryUsage memoryUsageHeap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
                long maxHeapSet = memoryUsageHeap.getMax();
                long totalPhysicalRam = ((OperatingSystemMXBean) (ManagementFactory.getOperatingSystemMXBean())).getTotalPhysicalMemorySize();
                if (maxHeapSet > 0 && totalPhysicalRam > 0) {
                    long available = totalPhysicalRam - maxHeapSet;
                    if (available > 0) {
                        if (available <= calculatedSizeForDataRegion) {
                            calculatedSizeForDataRegion = (long) Math.ceil(DATA_REGION_PERCENT * available);
                            log.error("available bytes={} is less than offHeapDataRegionMb={} ! maxHeapSet={}, totalPhysicalRam={}, new calculatedSizeForDataRegion={}",
                                    available, offHeapDataRegionMb, maxHeapSet, totalPhysicalRam, calculatedSizeForDataRegion);
                        } else {
                            log.info("calculatedSizeForDataRegion={} from offHeapDataRegionMb={}",
                                    calculatedSizeForDataRegion, offHeapDataRegionMb);
                        }
                    } else {
                        log.error("Looks like too high Heap Usage! available={} is <= 0! will be used as set calculatedSizeForDataRegion={}! maxHeapSet={}, totalPhysicalRam={}",
                                available, calculatedSizeForDataRegion, maxHeapSet, totalPhysicalRam);
                    }
                } else {
                    log.error("Fail to get heap and non heap size! calculatedSizeForDataRegion={}, maxHeapSet={}, totalPhysicalRam={}",
                            calculatedSizeForDataRegion, maxHeapSet, totalPhysicalRam);
                }
            } catch (Exception e) {
                log.error("Fail to get heap and non heap size! calculatedSizeForDataRegion={}", calculatedSizeForDataRegion, e);
            }
            return calculatedSizeForDataRegion;
        }

        @Bean
        @Profile({ProfileConfigConstants.SPRING_PROFILE_TEST})
        public TcpDiscoveryIpFinder tcpDiscoveryLocal() {
            return new TcpDiscoveryVmIpFinder(true);
        }

        @Bean
        @Profile({ProfileConfigConstants.SPRING_PROFILE_DEVELOPMENT, ProfileConfigConstants.SPRING_PROFILE_PRODUCTION})
        public TcpDiscoveryIpFinder tcpDiscoveryIpFinder(Environment environment) {
            if (Arrays.asList(environment.getActiveProfiles()).contains(ProfileConfigConstants.SPRING_PROFILE_K8S)) {
                return tcpDiscoveryIpFinderK8S();
            }

            TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder(true);
            if (null != discoveryProps.getNodes()) {
                ipFinder.setAddresses(discoveryProps.getNodes());
            }
            return ipFinder;
        }

        @Bean
        @Profile({ProfileConfigConstants.SPRING_PROFILE_K8S})
        public TcpDiscoveryIpFinder tcpDiscoveryIpFinderK8S() {
            TcpDiscoveryKubernetesIpFinder ipFinder = new TcpDiscoveryKubernetesIpFinder();
            ipFinder.setShared(kubernetesProperties.isShared());
            ipFinder.setAccountToken(kubernetesProperties.getAccountToken());
            ipFinder.setMasterUrl(kubernetesProperties.getMasterUrl());
            ipFinder.setNamespace(kubernetesProperties.getNamespace());
            ipFinder.setServiceName(kubernetesProperties.getServiceName());
            return ipFinder;
        }

        @Bean
        public TcpDiscoverySpi tcpDiscoverySpi(TcpDiscoveryIpFinder tcpDiscoveryIpFinder, Optional<AddressResolver> addressResolver) {
            TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
            tcpDiscoverySpi.setIpFinder(tcpDiscoveryIpFinder);
            tcpDiscoverySpi.setLocalPort(discoveryProps.getLocalPort());
            tcpDiscoverySpi.setLocalPortRange(discoveryProps.getLocalPortRange());
            tcpDiscoverySpi.setSocketTimeout(discoveryProps.getSocketTimeout());
            tcpDiscoverySpi.setMaxAckTimeout(discoveryProps.getMaxAckTimeout());
            tcpDiscoverySpi.setAckTimeout(discoveryProps.getAckTimeout());
            tcpDiscoverySpi.setReconnectCount(discoveryProps.getReconnectCount());
            tcpDiscoverySpi.setNetworkTimeout(discoveryProps.getNetworkTimeout());
            addressResolver.ifPresent(tcpDiscoverySpi::setAddressResolver);
            return tcpDiscoverySpi;
        }

        @Bean
        public TcpCommunicationSpi tcpCommunicationSpi(Optional<AddressResolver> addressResolver) {
            TcpCommunicationSpi tcpCommunicationSpi = new TcpCommunicationSpi();
            tcpCommunicationSpi.setLocalPort(communicationProps.getLocalPort());
            tcpCommunicationSpi.setLocalPortRange(communicationProps.getLocalPortRange());
            tcpCommunicationSpi.setSharedMemoryPort(communicationProps.getSharedMemoryPort());
            tcpCommunicationSpi.setConnectTimeout(communicationProps.getConnectTimeout());
            tcpCommunicationSpi.setMaxConnectTimeout(communicationProps.getMaxConnectTimeout());
            tcpCommunicationSpi.setReconnectCount(communicationProps.getReconnectCount());
            addressResolver.ifPresent(tcpCommunicationSpi::setAddressResolver);
            return tcpCommunicationSpi;
        }

        @Bean
        public DataStorageConfiguration dataStorageConfiguration() {
            DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration();
            dataStorageConfiguration.setStoragePath(dataStorageProperties.getStoragePath());
            dataStorageConfiguration.setWalPath(dataStorageProperties.getWalPath());
            dataStorageConfiguration.setWalArchivePath(dataStorageProperties.getWalArchivePath());

            DataRegionConfiguration dataRegionConfiguration = new DataRegionConfiguration();
            dataRegionConfiguration.setMaxSize(calculateNonHeapMemoryForDataRegion(dataStorageProperties.memoryOffHeapMaxMb));
            dataRegionConfiguration.setMetricsEnabled(true);
            dataRegionConfiguration.setPersistenceEnabled(dataStorageProperties.isPersistenceEnabled());
            dataStorageConfiguration.setDefaultDataRegionConfiguration(dataRegionConfiguration);
            return dataStorageConfiguration;
        }
    }
}
