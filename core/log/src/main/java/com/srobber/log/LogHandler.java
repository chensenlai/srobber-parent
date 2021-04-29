package com.srobber.log;

/**
 * 日志处理类
 * @author chensenlai
 * 2021-04-28 下午2:21
 */
public interface LogHandler {

    /**
     * 日志处理
     * @param event 日志事件
     */
    void handle(LogEvent event);
}
