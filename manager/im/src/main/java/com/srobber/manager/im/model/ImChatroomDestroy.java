package com.srobber.manager.im.model;

import lombok.Data;

/**
 * 聊天室销毁请求
 * 销毁后, 聊天室成员不能再接收消息
 *
 * @author chensenlai
 * 2020-10-26 下午4:21
 */
@Data
public class ImChatroomDestroy {

    /**
     * 聊天室 Id
     */
    private String roomId;
}
