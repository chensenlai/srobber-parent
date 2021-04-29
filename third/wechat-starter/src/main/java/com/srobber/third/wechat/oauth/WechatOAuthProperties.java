package com.srobber.third.wechat.oauth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信授权配置参数
 *
 * @author chensenlai
 * 2020-09-29 下午7:21
 */
@Data
@ConfigurationProperties(prefix = "srobber.third.wechat.oauth")
public class WechatOAuthProperties {

    /**
     * 是否启用微信OAuth2配置
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
