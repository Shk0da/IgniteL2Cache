package com.github.shk0da.demo.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executors;

@Slf4j
@EnableAsync
@Configuration
public class AsyncConfiguration {

    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    public static final int AVAILABLE_TASK_THREADS = AVAILABLE_PROCESSORS * 2;

    static {
        log.info("Available processors: {}", AVAILABLE_PROCESSORS);
        log.info("Available task threads: {}", AVAILABLE_TASK_THREADS);
    }

    @Primary
    @Bean("taskExecutor")
    public TaskExecutor taskExecutor() {
        ConcurrentTaskExecutor taskExecutor = new ConcurrentTaskExecutor();
        taskExecutor.setConcurrentExecutor(Executors.newWorkStealingPool(AVAILABLE_TASK_THREADS));
        return taskExecutor;
    }

    @Bean("cachedThreadPoolExecutor")
    public TaskExecutor cachedThreadPoolExecutor() {
        ConcurrentTaskExecutor taskExecutor = new ConcurrentTaskExecutor();
        taskExecutor.setConcurrentExecutor(Executors.newCachedThreadPool(
                new ThreadFactoryBuilder().setNameFormat("main-task-executor-%d").build()
        ));
        return taskExecutor;
    }

    @Bean("taskScheduler")
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(AVAILABLE_TASK_THREADS);
        scheduler.setErrorHandler(throwable -> log.error("Scheduled task error", throwable));
        scheduler.setThreadNamePrefix("main-task-scheduler-");
        return scheduler;
    }
}
