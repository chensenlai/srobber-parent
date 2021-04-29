package com.srobber.third.agora.rtc;

import io.agora.media.RtcTokenBuilder;
import lombok.Setter;

/**
 * RTC Token：如果你使用的是 Agora RTC SDK、本地服务端录制 SDK、
 * 云端录制、实时码流加速 SDK 或互动游戏 SDK，则参考本文内容生成 RTC Token。
 *
 * @author chensenlai
 * 2020-10-20 上午10:33
 */
@Setter
public class RtcTokenThirdImpl implements RtcTokenThird {

    private static final int EXPIRATION_TIME_IN_SECONDS = 3600;

    private String appId;
    private String appCertificate;

    @Override
    public String getToken(long userId, long roomId, RtcTokenBuilder.Role role) {
        String userAccount = String.valueOf(userId);
        String channelName = String.valueOf(roomId);
        int timestamp = (int)(System.currentTimeMillis() / 1000 + EXPIRATION_TIME_IN_SECONDS);

        RtcTokenBuilder token = new RtcTokenBuilder();
        String result = token.buildTokenWithUserAccount(appId, appCertificate,
                channelName, userAccount, role, timestamp);
        return result;
    }
}
