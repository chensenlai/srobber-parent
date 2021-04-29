package com.srobber.log.support;

import com.srobber.log.LogEvent;
import com.srobber.log.LogHandler;
import com.srobber.log.enums.LogLevelEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * 日志事件监听器
 *
 * @author chensenlai
 * 2020-09-24 下午3:36
 */
@Slf4j
public class LoggerLogHandler implements LogHandler {

    @Override
    public void handle(LogEvent event) {
        if(event.getLevel() == LogLevelEnum.Info) {
            log.info("#log: {}", event);
        } else if(event.getLevel() == LogLevelEnum.Warn) {
            log.warn("#log: {}", event);
        } else if(event.getLevel() == LogLevelEnum.Error) {
            log.error("#log: {}", event);
        }
    }
}
