package com.srobber.manager.phone;

import com.srobber.manager.phone.aliyun.AliyunPhoneManager;
import com.srobber.manager.phone.shanyan.ShanyanPhoneManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 号码自动配置
 *
 * @author chensenlai
 */
@Configuration
@EnableConfigurationProperties(PhoneProperties.class)
public class PhoneAutoConfiguration {

    @Bean
    @Primary
    @ConditionalOnMissingBean(name="phoneManager")
    @ConditionalOnProperty(prefix = "srobber.manager.phone", name = "type", havingValue = "aliyun", matchIfMissing = true)
    public PhoneManager aliyunPhoneManager(PhoneProperties phoneProperties) {
        PhoneProperties.Aliyun aliyun = phoneProperties.getAliyun();
        AliyunPhoneManager phoneManager = new AliyunPhoneManager();
        phoneManager.setRegionId(aliyun.getRegionId());
        phoneManager.setAccessKeyId(aliyun.getAccessKeyId());
        phoneManager.setAccessSecret(aliyun.getAccessSecret());
        return phoneManager;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(name="phoneManager")
    @ConditionalOnProperty(prefix = "srobber.manager.phone", name = "type", havingValue = "shanyan", matchIfMissing = false)
    public PhoneManager shanyanPhoneManager(PhoneProperties phoneProperties) {
        PhoneProperties.Shanyan shanyan = phoneProperties.getShanyan();
        ShanyanPhoneManager phoneManager = new ShanyanPhoneManager();
        phoneManager.setAppId(shanyan.getAppId());
        phoneManager.setAppKey(shanyan.getAppKey());
        phoneManager.setEncryptType(shanyan.getEncryptType());
        phoneManager.setPrivateKey(shanyan.getPrivateKey());
        return phoneManager;
    }
}
