package com.github.shk0da.demo.config;

import com.github.shk0da.demo.persistence.RoutingDataSource;
import com.google.common.collect.ImmutableMap;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;

import javax.sql.DataSource;
import java.util.Map;

import static com.github.shk0da.demo.util.DatabaseUtil.checkDataSource;

@Slf4j
@EnableRetry
@Configuration
@EntityScan(basePackages = {"com.github.shk0da.demo.domain"})
@EnableJpaRepositories(basePackages = {"com.github.shk0da.demo.repository"})
public class DataSourceConfiguration {

    private static final int DEFAULT_POOL_SIZE = 10;
    private static final int DEFAULT_REPLICA_SIZE = 3;
    private static final String PRIMARY_DATASOURCE_PREFIX = "spring.primary.datasource";
    private static final String REPLICA_DATASOURCE_PREFIX = "spring.replica.datasource";

    private final Environment environment;

    public DataSourceConfiguration(Environment environment, Ignite ignite) {
        this.environment = environment;
        log.info("Ignite: {}", ignite.name());
    }

    @Recover
    public void recover(Exception ex) {
        log.error("Failed to get or prepare connection", ex);
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        final DataSource primaryDataSource = primaryDataSource();
        final DataSource replicaDataSource = replicaDataSource();
        final Map<Object, Object> targetDataSources = ImmutableMap.of(
                RoutingDataSource.Route.PRIMARY, primaryDataSource,
                RoutingDataSource.Route.REPLICA, replicaDataSource
        );
        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(primaryDataSource);
        return routingDataSource;
    }

    private DataSource primaryDataSource() {
        return checkAndGetDataSource(buildDataSource(
                PRIMARY_DATASOURCE_PREFIX,
                RoutingDataSource.Route.PRIMARY.name() + "Pool",
                Math.max(DEFAULT_POOL_SIZE, AsyncConfiguration.AVAILABLE_TASK_THREADS)
        ));
    }

    private DataSource replicaDataSource() {
        return checkAndGetDataSource(buildDataSource(
                REPLICA_DATASOURCE_PREFIX,
                RoutingDataSource.Route.REPLICA.name() + "Pool",
                Math.max(DEFAULT_POOL_SIZE, AsyncConfiguration.AVAILABLE_TASK_THREADS) * DEFAULT_REPLICA_SIZE
        ));
    }

    private HikariDataSource buildDataSource(String dataSourcePrefix, String poolName, int poolSize) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName(poolName);
        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setJdbcUrl(environment.getProperty(String.format("%s.url", dataSourcePrefix)));
        hikariConfig.setUsername(environment.getProperty(String.format("%s.username", dataSourcePrefix)));
        hikariConfig.setPassword(environment.getProperty(String.format("%s.password", dataSourcePrefix)));
        hikariConfig.setDriverClassName(environment.getProperty(String.format("%s.driver-class-name", dataSourcePrefix)));
        return new HikariDataSource(hikariConfig);
    }

    @NotNull
    private DataSource checkAndGetDataSource(HikariDataSource dataSource) {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName(dataSource.getDriverClassName());
        driverManagerDataSource.setUsername(dataSource.getUsername());
        driverManagerDataSource.setPassword(dataSource.getPassword());
        driverManagerDataSource.setUrl(dataSource.getJdbcUrl());
        checkDataSource(driverManagerDataSource, dataSource.getPoolName());
        return dataSource;
    }
}
