package com.srobber.manager.im.rongcloud.message;

import com.srobber.common.util.JsonUtil;
import io.rong.messages.BaseMessage;

/**
 * 单聊消息定义
 * @author chensenlai
 * 2020-10-29 下午6:49
 */
public class ImPrivateContent extends BaseMessage {

    /**
     * 私聊消息自定义类型
     */
    private static transient final String IM_PRIVATE_TYPE = "ImPrivate:ctype";

    /**
     * 消息编码
     */
    private String code = "";
    /**
     * 消息内容
     */
    private String data = "";

    public ImPrivateContent(String code, String data) {
        this.code = code;
        this.data = data;
    }

    @Override
    public String getType() {
        return IM_PRIVATE_TYPE;
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
