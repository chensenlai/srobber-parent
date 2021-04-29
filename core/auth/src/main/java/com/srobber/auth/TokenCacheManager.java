package com.srobber.auth;

/**
 * 登录token缓存管理器
 *
 * @author chensenlai
 * 2020-09-18 下午1:31
 */
public interface TokenCacheManager {
    /**
     * 存储用户登陆信息
     * @param loginInfo 用户登陆信息
     * @return 访问凭证
     */
    String putLoginInfo(UserLoginInfo loginInfo);

    /**
     * token凭证换取用户登陆信息
     * @param token 访问凭证
     * @return 用户登陆信息
     */
    UserLoginInfo getLoginInfo(String token);

    /**
     * 删除用户登录信息
     * @param token 访问凭证
     */
    void deleteLoginInfo(String token);
}
