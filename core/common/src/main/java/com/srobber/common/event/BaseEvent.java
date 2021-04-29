package com.srobber.common.event;

import org.springframework.context.ApplicationEvent;

/**
 * 事件
 * @author chensenlai
 * 2020-09-23 下午5:08
 */
public abstract class BaseEvent extends ApplicationEvent {

    private static final Object EMPTY = new Object();

    public BaseEvent() {
        super(EMPTY);
    }
}
