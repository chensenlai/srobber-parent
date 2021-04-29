package com.srobber.manager.im.rongcloud.message;

import com.srobber.common.util.JsonUtil;
import io.rong.messages.BaseMessage;

/**
 * 聊天室消息内容
 * @author chensenlai
 * 2020-10-29 下午6:59
 */
public class ImChatroomContent extends BaseMessage {

    /**
     * 房间消息自定义类型
     */
    private static transient final String IM_CHATROOM_TYPE = "ImChatroom:ctype";

    /**
     * 消息编码
     */
    private String code = "";
    /**
     * 消息内容
     */
    private String data = "";

    public ImChatroomContent(String code, String data) {
        this.code = code;
        this.data = data;
    }

    @Override
    public String getType() {
        return IM_CHATROOM_TYPE;
    }

    public String getCode() {
        return code;
    }
    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return JsonUtil.toStr(this);
    }
}
