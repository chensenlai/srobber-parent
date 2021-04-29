package com.srobber.third.wechat.jsapi;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 微信jsapi自动配置
 * @author chensenlai
 * 2020-12-11 上午11:47
 */
@Configuration
@EnableConfigurationProperties(WechatJsapiProperties.class)
@ConditionalOnProperty(prefix = "srobber.third.wechat.jsapi", name = "enabled", matchIfMissing = false)
public class WechatJsapiAutoConfiguration {

    private final WechatJsapiProperties wechatJsapiProperties;

    public WechatJsapiAutoConfiguration(WechatJsapiProperties wechatJsapiProperties) {
        this.wechatJsapiProperties = wechatJsapiProperties;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(name="wechatJsapiThird")
    public WechatJsapiThird wechatJsapiThird() {
        WechatJsapiThirdImpl wechatJsapiThird = new WechatJsapiThirdImpl();
        wechatJsapiThird.setAppid(this.wechatJsapiProperties.getAppid());
        wechatJsapiThird.setAppSecret(this.wechatJsapiProperties.getAppSecret());
        return wechatJsapiThird;
    }
}
