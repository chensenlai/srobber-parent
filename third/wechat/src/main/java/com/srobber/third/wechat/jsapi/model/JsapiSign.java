package com.srobber.third.wechat.jsapi.model;

import com.srobber.common.util.RandomUtil;
import lombok.Data;

/**
 * 微信JSAPI签名
 * @author chensenlai
 * 2020-12-11 上午11:34
 */
@Data
public class JsapiSign {

    private String appid;
    private String noncestr;
    private int timestamp;
    private String signature;

    public JsapiSign(int ts, String acc) {
        noncestr = RandomUtil.randomNumeric(16);
        timestamp = ts;
        appid = acc;
    }
}
