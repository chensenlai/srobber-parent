package com.srobber.manager.pay;

import com.srobber.manager.pay.apple.ApplePayManagerImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 苹果支付自动配置
 *
 * @author chensenlai
 * 2020-10-12 下午4:02
 */
@Configuration
@EnableConfigurationProperties(ApplePayProperties.class)
@ConditionalOnProperty(prefix = "srobber.manager.pay.apple", name = "enabled", matchIfMissing = true)
public class ApplePayAutoConfiguration {

    @Bean
    @Primary
    @ConditionalOnMissingBean(name="applePayManager")
    public ApplePayManager applePayManager(ApplePayProperties applePayProperties) {
        ApplePayManagerImpl applePayManager = new ApplePayManagerImpl();
        applePayManager.setPassword(applePayProperties.getPassword());
        return applePayManager;
    }
}
