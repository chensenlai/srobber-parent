package com.srobber.manager.im.model;

import lombok.Data;

/**
 * 发送聊天室消息请求
 *
 * @author chensenlai
 * 2020-10-26 下午5:03
 */
@Data
public class ImMessageChatroom {

    /**
     * 发送用户编号
     */
    private String sendUserId;
    /**
     * 聊天室编号
     */
    private String roomId;
    /**
     * 消息定义码
     */
    private String code;
    /**
     * 消息数据
     */
    private Object content;
}
