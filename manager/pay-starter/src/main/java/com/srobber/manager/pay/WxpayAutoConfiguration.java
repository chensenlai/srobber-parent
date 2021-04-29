package com.srobber.manager.pay;

import com.srobber.manager.pay.wxpay.WxpayManagerImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 微信支付自动配置
 *
 * @author chensenlai
 */
@Configuration
@EnableConfigurationProperties(WxpayProperties.class)
@ConditionalOnProperty(prefix = "srobber.manager.pay.wxpay", name = "enabled", matchIfMissing = true)
public class WxpayAutoConfiguration {

    @Bean
    @Primary
    @ConditionalOnMissingBean(name="wxpayManager")
    public WxpayManager wxpayManager(WxpayProperties wxpayProperties) {
        WxpayManagerImpl wxpayManager = new WxpayManagerImpl();
        wxpayManager.setMchId(wxpayProperties.getMchId());
        wxpayManager.setAppId(wxpayProperties.getAppId());
        wxpayManager.setKey(wxpayProperties.getKey());
        wxpayManager.setNotifyUrl(wxpayProperties.getNotifyUrl());
        wxpayManager.setSignType(wxpayProperties.getSignType());
        wxpayManager.init();
        return wxpayManager;
    }
}
