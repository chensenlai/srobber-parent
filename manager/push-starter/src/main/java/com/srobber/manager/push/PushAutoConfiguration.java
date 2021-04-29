package com.srobber.manager.push;

import com.srobber.manager.push.jpush.JpushPushClient;
import com.srobber.manager.push.umeng.UmengPushClient;
import com.srobber.manager.push.xinge.XingePushClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 推送自动配置
 *
 * @author chensenlai
 */
@Configuration
@EnableConfigurationProperties(PushProperties.class)
public class PushAutoConfiguration {

    private final PushProperties pushProperties;

    public PushAutoConfiguration(PushProperties pushProperties) {
        this.pushProperties = pushProperties;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(name="pushManager")
    public PushManager pushManager(PushClient pushClient) {
        PushManagerImpl pushManager = new PushManagerImpl();
        pushManager.setPushClient(pushClient);
        return pushManager;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(name="pushClient")
    @ConditionalOnProperty(prefix = "srobber.manager.push", name = "type", havingValue = "umeng", matchIfMissing = true)
    public PushClient umengPushClient() {
        PushProperties.Umeng umeng = pushProperties.getUmeng();
        UmengPushClient pushClient = new UmengPushClient();
        pushClient.setAndroidAppkey(umeng.getAndroidAppkey());
        pushClient.setAndroidAppMasterSecret(umeng.getAndroidAppMasterSecret());
        pushClient.setIosAppkey(umeng.getIosAppkey());
        pushClient.setIosAppMasterSecret(umeng.getIosAppMasterSecret());
        return pushClient;
    }

    @Bean
    @ConditionalOnMissingBean(name="pushClient")
    @ConditionalOnProperty(prefix = "srobber.manager.push", name = "type", havingValue = "xinge", matchIfMissing = false)
    public PushClient xingePushClient() {
        PushProperties.Xinge xingge = pushProperties.getXinge();
        XingePushClient pushClient = new XingePushClient();
        pushClient.setAndroidAppId(xingge.getAndroidAppId());
        pushClient.setAndroidSecretKey(xingge.getAndroidSecretKey());
        pushClient.setIosAppId(xingge.getIosAppId());
        pushClient.setIosSecretKey(xingge.getIosSecretKey());
        pushClient.init();
        return pushClient;
    }

    @Bean
    @ConditionalOnMissingBean(name="pushClient")
    @ConditionalOnProperty(prefix = "srobber.manager.push", name = "type", havingValue = "jpush", matchIfMissing = false)
    public PushClient jpushPushClient() {
        PushProperties.Jpush jpush = pushProperties.getJpush();
        JpushPushClient pushClient = new JpushPushClient();
        pushClient.setMasterSecret(jpush.getMasterSecret());
        pushClient.setAppKey(jpush.getAppKey());
        pushClient.init();
        return pushClient;
    }
}
