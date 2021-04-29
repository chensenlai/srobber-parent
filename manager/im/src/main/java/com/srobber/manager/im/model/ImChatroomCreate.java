package com.srobber.manager.im.model;

import lombok.Data;

/**
 * 聊天室创建请求
 *
 * @author chensenlai
 * 2020-10-26 下午4:19
 */
@Data
public class ImChatroomCreate {

    /**
     * 聊天室 Id
     */
    private String roomId;
    /**
     * 聊天室名称
     */
    private String name;
}
