package com.srobber.manager.im.model;

import lombok.Data;

/**
 * 发送单个人消息请求
 *
 * @author chensenlai
 * 2020-10-29 下午5:14
 */
@Data
public class ImMessagePrivate {

    /**
     * 发送用户编号
     */
    private String sendUserId;
    /**
     * 接收用户编号
     */
    private String[] receiveUserIds;
    /**
     * 消息码
     */
    private String code;
    /**
     * 消息数据
     */
    private Object content;
    /**
     * 推送数据
     */
    private String pushData;
}
