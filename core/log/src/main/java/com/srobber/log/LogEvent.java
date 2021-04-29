package com.srobber.log;

import com.srobber.common.event.BaseEvent;
import com.srobber.log.enums.LogLevelEnum;
import lombok.Data;
import lombok.ToString;

/**
 * 日志事件记录
 *
 * @author chensenlai
 */
@Data
@ToString
public class LogEvent extends BaseEvent {

    private String type;
    private LogLevelEnum level;
    private String key1;
    private String key2;
    private String key3;
    private String data;
    private String exception;
}
