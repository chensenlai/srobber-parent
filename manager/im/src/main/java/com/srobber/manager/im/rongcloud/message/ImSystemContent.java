package com.srobber.manager.im.rongcloud.message;

import com.srobber.common.util.JsonUtil;
import io.rong.messages.BaseMessage;

/**
 * 系统消息定义
 * @author chensenlai
 * 2020-10-29 下午7:00
 */
public class ImSystemContent extends BaseMessage {

    /**
     * 系统消息自定义类型
     */
    private static transient final String IM_SYSTEM_TYPE = "ImSystem:ctype";

    /**
     * 消息编码
     */
    private String code = "";
    /**
     * 消息内容
     */
    private String data = "";

    public ImSystemContent(String code, String data) {
        this.code = code;
        this.data = data;
    }

    @Override
    public String getType() {
        return IM_SYSTEM_TYPE;
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
