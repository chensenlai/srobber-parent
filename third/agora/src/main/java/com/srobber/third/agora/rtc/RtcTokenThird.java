package com.srobber.third.agora.rtc;

import io.agora.media.RtcTokenBuilder;

/**
 * RTC Token：如果你使用的是 Agora RTC SDK、本地服务端录制 SDK、
 * 云端录制、实时码流加速 SDK 或互动游戏 SDK，则参考本文内容生成 RTC Token。
 *
 * @author chensenlai
 * 2020-10-20 上午10:33
 */
public interface RtcTokenThird {

    /**
     * 获取token
     *
     * @param userId 用户编号
     * @param roomId 房间编号
     * @param role   角色(是否有发流权限)
     *               Role_Publisher(1)：（默认）用户有发流权限。
     *               Role_Subscriber(2)：用户没有发流权限。
     * @return token
     */
    String getToken(long userId, long roomId, RtcTokenBuilder.Role role);

}