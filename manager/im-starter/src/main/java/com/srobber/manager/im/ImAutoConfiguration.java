package com.srobber.manager.im;

import com.srobber.manager.im.rongcloud.RongcloudChatroomManager;
import com.srobber.manager.im.rongcloud.RongcloudMessageManager;
import com.srobber.manager.im.rongcloud.RongcloudUserManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * IM自动配置
 *
 * @author chensenlai
 * 2020-10-26 下午6:32
 */
@Configuration
@EnableConfigurationProperties(ImProperties.class)
public class ImAutoConfiguration {

    @Bean
    @Primary
    @ConditionalOnMissingBean(name="imUserManager")
    @ConditionalOnProperty(prefix = "srobber.manager.im", name = "type", havingValue = "rongcloud", matchIfMissing = true)
    public ImUserManager rongloudImUserManager(ImProperties imProperties) {
        ImProperties.Rongcloud rongcloud = imProperties.getRongcloud();
        RongcloudUserManager imUserManager = new RongcloudUserManager();
        imUserManager.setAppKey(rongcloud.getAppKey());
        imUserManager.setAppSecret(rongcloud.getAppSecret());
        return imUserManager;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(name="imChatroomManager")
    @ConditionalOnProperty(prefix = "srobber.manager.im", name = "type", havingValue = "rongcloud", matchIfMissing = true)
    public ImChatroomManager rongcloudImChatroomManager(ImProperties imProperties) {
        ImProperties.Rongcloud rongcloud = imProperties.getRongcloud();
        RongcloudChatroomManager imChatroomManager = new RongcloudChatroomManager();
        imChatroomManager.setAppKey(rongcloud.getAppKey());
        imChatroomManager.setAppSecret(rongcloud.getAppSecret());
        return imChatroomManager;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(name="imMessageManager")
    @ConditionalOnProperty(prefix = "srobber.manager.im", name = "type", havingValue = "rongcloud", matchIfMissing = true)
    public ImMessageManager rongcloudImMessageManager(ImProperties imProperties) {
        ImProperties.Rongcloud rongcloud = imProperties.getRongcloud();
        RongcloudMessageManager imMessageManager = new RongcloudMessageManager();
        imMessageManager.setAppKey(rongcloud.getAppKey());
        imMessageManager.setAppSecret(rongcloud.getAppSecret());
        return imMessageManager;
    }
}
