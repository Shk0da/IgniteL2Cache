package com.github.shk0da.demo.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Slf4j
@Configuration
@AllArgsConstructor
@SpringBootConfiguration
public class DemoApplicationConfiguration {

    private final ApplicationContext applicationContext;

    @PostConstruct
    void defaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @EventListener
    public void onStartup(ApplicationReadyEvent event) {
        log.info("Started {}!", applicationContext.getId());
        shutdownHook();
    }

    @EventListener
    public void onShutdown(ContextStoppedEvent event) {
        log.warn("Shutdown {}!", applicationContext.getId());
    }

    @Async
    protected void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            onShutdown(new ContextStoppedEvent(applicationContext));
            SpringApplication.exit(applicationContext);
        }, "shutdown-hook"));
    }
}
