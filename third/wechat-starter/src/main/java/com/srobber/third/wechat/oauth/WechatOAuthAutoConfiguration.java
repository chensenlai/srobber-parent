package com.srobber.third.wechat.oauth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 微信授权自动配置
 *
 * @author chensenlai
 * 2020-09-29 下午7:13
 */
@Configuration
@EnableConfigurationProperties(WechatOAuthProperties.class)
@ConditionalOnProperty(prefix = "srobber.third.wechat.oauth", name = "enabled", matchIfMissing = false)
public class WechatOAuthAutoConfiguration {

    private final WechatOAuthProperties wechatOAuthProperties;

    public WechatOAuthAutoConfiguration(WechatOAuthProperties wechatOAuthProperties) {
        this.wechatOAuthProperties = wechatOAuthProperties;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(name="wechatOAuthThird")
    public WechatOAuthThird wechatOAuthThird() {
        WechatOAuthThirdImpl wechatOAuthThird = new WechatOAuthThirdImpl();
        wechatOAuthThird.setAppid(this.wechatOAuthProperties.getAppid());
        wechatOAuthThird.setAppSecret(this.wechatOAuthProperties.getAppSecret());
        return wechatOAuthThird;
    }
}
