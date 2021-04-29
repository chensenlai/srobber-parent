package com.srobber.third.wechat.jsapi;

import com.srobber.third.wechat.jsapi.model.JsapiSign;

/**
 * 微信jsapi管理
 * @author chensenlai
 * 2020-12-11 上午11:30
 */
public interface WechatJsapiThird {

    /**
     * 微信jsapi签名
     * @param url 分享URL
     * @return 签名结果
     */
    JsapiSign sign(String url);

}
