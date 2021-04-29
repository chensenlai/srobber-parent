package com.srobber.third.wechat.jsapi;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信jsapi配置参数
 *
 * @author chensenlai
 * 2020-12-11 上午11:46
 */
@Data
@ConfigurationProperties(prefix = "srobber.third.wechat.jsapi")
public class WechatJsapiProperties {

    /**
     * 是否启用微信jsapi配置
     */
    private Boolean enabled;
    /**
     * 微信应用appid
     */
    private String appid;
    /**
     * 微信应用appSecret
     */
    private String appSecret;
}
