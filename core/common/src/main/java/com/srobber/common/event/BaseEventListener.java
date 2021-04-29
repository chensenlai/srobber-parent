package com.srobber.common.event;

import org.springframework.context.ApplicationListener;

/**
 * 事件监听器
 * @author chensenlai
 * 2020-09-24 下午3:17
 */
public abstract class BaseEventListener<T extends BaseEvent> implements ApplicationListener<T> {

    @Override
    public void onApplicationEvent(T event) {
        onEvent(event);
    }

    /**
     * 事件触发
     * @param event 事件
     */
    public abstract void onEvent(T event);
}
