package com.srobber.auth;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户登陆信息存储管理
 *
 * @author chensenlai
 */
public interface UserLoginInfoStore {

    /**
     * 解析和存储用户UserLoginInfo
     * @param req 用户请求
     * @return UserLoginInfo
     */
    UserLoginInfo parseAndStore(HttpServletRequest req);

    /**
     * 加载用户UserLoginInfo
     * @param req 用户请求
     * @return UserLoginInfo
     */
    UserLoginInfo load(HttpServletRequest req);
}
