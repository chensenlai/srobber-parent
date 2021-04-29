package com.srobber.manager.im.model;

import lombok.Data;

/**
 * 系统消息请求
 *
 * @author chensenlai
 * 2020-10-26 下午6:06
 */
@Data
public class ImMessageSystem {

    /**
     * 发送用户编号
     * (用户存在,则显示用户昵称, 用户不存在,则显示应用名称)
     */
    private String sendUserId;
    /**
     * 接收用户编号
     */
    private String[] receiveUserIds;
    /**
     * 消息定义码
     */
    private String code;
    /**
     * 消息内容(离线推送显示在通知栏)
     */
    private String content;
    /**
     * 推送数据
     */
    private String pushData;
}
