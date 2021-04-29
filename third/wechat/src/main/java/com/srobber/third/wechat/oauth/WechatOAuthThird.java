package com.srobber.third.wechat.oauth;

import com.srobber.third.wechat.oauth.model.AccessToken;
import com.srobber.third.wechat.oauth.model.UserInfo;

/**
 * 微信授权帐号管理
 *
 * @author chensenlai
 * 2020-09-17 下午7:40
 */
public interface WechatOAuthThird {

    /**
     * 获取Access_Token（oAuth认证,此access_token与基础支持的access_token不同）
     * @param code 用户授权码
     * @return 返回null, 表示获取accessToken失败
     */
    AccessToken getAccessToken(String code);

    /**
     * 获取用户基础信息
     * @param accessToken 访问token
     * @param openid 用户openid
     * @return 返回null, 表示获取用户信息失败
     */
    UserInfo getUserInfo(String accessToken, String openid);
}
