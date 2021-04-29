package com.srobber.third.agora.rtc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 *
 * RTC Token：如果你使用的是 Agora RTC SDK、本地服务端录制 SDK、
 * 云端录制、实时码流加速 SDK 或互动游戏 SDK，则参考本文内容生成 RTC Token。
 *
 * @author chensenlai
 * 2020-10-20 上午10:56
 */
@Configuration
@EnableConfigurationProperties(RtcTokenProperties.class)
@ConditionalOnProperty(prefix = "srobber.third.agora.rtc", name = "enabled", matchIfMissing = false)
public class RtcTokenAutoConfiguration {

    private final RtcTokenProperties rtcTokenProperties;

    public RtcTokenAutoConfiguration(RtcTokenProperties rtcTokenProperties) {
        this.rtcTokenProperties = rtcTokenProperties;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(name="rtcTokenThird")
    public RtcTokenThird rtcTokenThird() {
        RtcTokenThirdImpl rtcTokenThird = new RtcTokenThirdImpl();
        rtcTokenThird.setAppId(this.rtcTokenProperties.getAppId());
        rtcTokenThird.setAppCertificate(this.rtcTokenProperties.getAppCertificate());
        return rtcTokenThird;
    }
}
