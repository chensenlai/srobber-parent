package com.srobber.third.apple.oauth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 苹果授权自动配置
 *
 * @author chensenlai
 * 2020-09-29 下午7:13
 */
@Configuration
@EnableConfigurationProperties(AppleOAuthProperties.class)
@ConditionalOnProperty(prefix = "srobber.third.apple.oauth", name = "enabled", matchIfMissing = false)
public class AppleOAuthAutoConfiguration {

    @Bean
    @Primary
    @ConditionalOnMissingBean(name="appleOAuthThird")
    public AppleOAuthThird appleOAuthThird() {
        AppleOAuthThirdImpl appleOAuthThird = new AppleOAuthThirdImpl();
        return appleOAuthThird;
    }
}
