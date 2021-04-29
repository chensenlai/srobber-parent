package com.srobber.third.wechat.jsapi.model;

import lombok.Data;

/**
 * 微信JSAPI token
 * @author chensenlai
 * 2020-12-11 上午11:32
 */
@Data
public class TokenTicket {
    private int errcode;
    private String errmsg;
    private String ticket;
    private String access_token;
    private int expires_in;
}
