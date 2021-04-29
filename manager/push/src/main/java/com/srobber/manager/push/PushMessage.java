package com.srobber.manager.push;

import lombok.Data;

import java.util.Map;

/**
 * 推送消息定义
 *
 * @author chensenlai
 */
@Data
public class PushMessage {

    private MessageType messageType;
    private String tip;
    private String title;
    private String content;
    /**
     * 通知或消息点击动作
     */
    private ClickAction clickAction;
    private String clickParam;

    /**
     * 扩展参数
     * 1 客户端约定的参数
     * 2 厂商离线推送参数
     *  eg: mipush / mi_activity
     */
    private Map<String, String> extraParams;

    /**
     * 推送消息类型
     */
    public enum MessageType {

        /**
         * 通知栏消息
         */
        NOTIFY,
        /**
         * 透传消息/静默消息
         */
        MESSAGE
        ;
    }

    /**
     * 点击推送触发动作
     */
    public enum ClickAction {
        /**
         * 打开APP
         */
        App,
        /**
         * 打开指定APP页面
         */
        ACTIVITY,
        /**
         * 打开网页
         */
        URL,
        /**
         * 自定义
         */
        CUSTOM
        ;
    }

}
