package com.srobber.third.apple.oauth;

import com.srobber.third.apple.oauth.model.UserIdentity;

/**
 * 苹果帐号授权登陆管理器
 *
 * @author chensenlai
 * 2020-09-18 上午9:51
 */
public interface AppleOAuthThird {


    /**
     * 获取苹果用户身份标识
     * @param identityTokenJwt 身份识串
     * @return 苹果ID
     */
    UserIdentity getUserIdentity(String identityTokenJwt);
}