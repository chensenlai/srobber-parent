package com.srobber.manager.sms;

import com.srobber.manager.sms.aliyun.AliyunSmsManager;
import com.srobber.manager.sms.xuanwu.XuanwuSmsManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 短信自动配置
 *
 * @author chensenlai
 */
@Configuration
@EnableConfigurationProperties(SmsProperties.class)
public class SmsAutoConfiguration {

    @Bean
    @Primary
    @ConditionalOnMissingBean(name="smsManager")
    @ConditionalOnProperty(prefix = "srobber.manager.sms", name = "type", havingValue = "aliyun", matchIfMissing = true)
    public SmsManager aliyunSmsManager(SmsProperties smsProperties) {
        SmsProperties.Aliyun aliyun = smsProperties.getAliyun();
        AliyunSmsManager smsManager = new AliyunSmsManager();
        smsManager.setAccessKeyId(aliyun.getAccessKeyId());
        smsManager.setAccessSecret(aliyun.getAccessSecret());
        smsManager.setSignName(aliyun.getSignName());
        return smsManager;
    }

    @Bean
    @ConditionalOnMissingBean(name="smsManager")
    @ConditionalOnProperty(prefix = "srobber.manager.sms", name = "type", havingValue = "xuanwu", matchIfMissing = false)
    public SmsManager xuanwuSmsManager(SmsProperties smsProperties) {
        SmsProperties.Xuanwu xuanwu = smsProperties.getXuanwu();
        XuanwuSmsManager smsManager = new XuanwuSmsManager();
        smsManager.setUser(xuanwu.getUser());
        smsManager.setPsw(xuanwu.getPsw());
        smsManager.setIp(xuanwu.getIp());
        smsManager.setMtPort(xuanwu.getMtPort());
        smsManager.setMoPort(xuanwu.getMoPort());
        smsManager.init();
        return smsManager;
    }
}
