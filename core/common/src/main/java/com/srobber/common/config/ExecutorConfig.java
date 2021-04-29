package com.srobber.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 * 业务公用线程池, 自定义线程池参数
 *
 * @author chensenlai
 */
@Slf4j
@Configuration
public class ExecutorConfig implements AsyncConfigurer {

    /**
     * 核心线程数
     */
    private int corePoolSize = CoreConfig.EXECUTOR_CORE_POOL_SIZE;
    /**
     * 最大线程数
     */
    private int maxPoolSize = CoreConfig.EXECUTOR_MAX_POOL_SIZE;
    /**
     * 等待队列长度
     */
    private int queueCapacity = CoreConfig.EXECUTOR_QUEUE_CAPACITY;
    /**
     * 线程idle保活时长(秒)
     */
    private int keepAliveSeconds = CoreConfig.EXECUTOR_KEEP_ALIVE_SECONDS;

    @Bean
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(corePoolSize);
        taskExecutor.setMaxPoolSize(maxPoolSize);
        taskExecutor.setQueueCapacity(queueCapacity);
        taskExecutor.setKeepAliveSeconds(keepAliveSeconds);
        taskExecutor.setThreadNamePrefix("business-pool-");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return taskExecutor;
    }

    /**
     * 任务异常处理器
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable,method,params)->{
            log.error("business-pool {} {} error.",
                    Thread.currentThread().getName(), method.getName(), throwable);
        };
    }
}
