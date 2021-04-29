package com.srobber.third.agora.rtc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RTC Token：如果你使用的是 Agora RTC SDK、本地服务端录制 SDK、
 * 云端录制、实时码流加速 SDK 或互动游戏 SDK，则参考本文内容生成 RTC Token。
 *
 * @author chensenlai
 * 2020-10-20 上午10:57
 */
@Data
@ConfigurationProperties(prefix = "srobber.third.agora.rtc")
public class RtcTokenProperties {

    private boolean enabled;
    private String appId;
    private String appCertificate;
}
