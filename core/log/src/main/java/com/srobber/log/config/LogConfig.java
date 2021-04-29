package com.srobber.log.config;

import com.srobber.log.LogAspect;
import com.srobber.log.LogHandler;
import com.srobber.log.support.LoggerLogHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 日志处理配置
 * @author chensenlai
 * 2021-04-28 下午2:19
 */
@Configuration
public class LogConfig {

    @Bean
    public LogAspect logAspect(LogHandler logHandler) {
        LogAspect logAspect = new LogAspect();
        logAspect.setLogHandler(logHandler);
        return logAspect;
    }

    @Bean
    @ConditionalOnMissingBean(name = "logHandler")
    public LogHandler logHandler() {
        LoggerLogHandler LogHandler = new LoggerLogHandler();
        return LogHandler;
    }
}
