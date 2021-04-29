package com.srobber.manager.im.model;

import lombok.Data;

/**
 * 用户是否在聊天室
 *
 * @author chensenlai
 * 2020-10-30 下午4:54
 */
@Data
public class ImChatroomUserExists {

    /**
     * 聊天室 Id
     */
    private String roomId;
    /**
     * 用户唯一标识
     */
    private String userId;
}
