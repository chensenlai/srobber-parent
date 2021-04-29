package com.srobber.manager.pay;

import com.srobber.manager.pay.alipay.AlipayManagerImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 支付宝支付自动配置
 *
 * @author chensenlai
 */
@Configuration
@EnableConfigurationProperties(AlipayProperties.class)
@ConditionalOnProperty(prefix = "srobber.manager.pay.alipay", name = "enabled", matchIfMissing = true)
public class AlipayAutoConfiguration {

    @Bean
    @Primary
    @ConditionalOnMissingBean(name="alipayManager")
    public AlipayManager alipayManager(AlipayProperties alipayProperties) {
        AlipayManagerImpl alipayManager = new AlipayManagerImpl();
        alipayManager.setSellerId(alipayProperties.getSellerId());
        alipayManager.setAppId(alipayProperties.getAppId());
        alipayManager.setAppPrivateKey(alipayProperties.getAppPrivateKey());
        alipayManager.setAlipayPublicKey(alipayProperties.getAlipayPublicKey());
        alipayManager.setReturnUrl(alipayProperties.getReturnUrl());
        alipayManager.setNotifyUrl(alipayProperties.getNotifyUrl());
        alipayManager.setSignType(alipayProperties.getSignType());
        alipayManager.init();
        return alipayManager;
    }
}
