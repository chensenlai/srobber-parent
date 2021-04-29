package com.srobber.auth;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户UserAgent存储器
 *
 * @author chensenlai
 */
public interface UserAgentInfoStore {

    /**
     * 解析和存储用户UserAgentInfo
     * @param req 用户请求
     * @return UserAgentInfo
     */
    UserAgentInfo parseAndSore(HttpServletRequest req);

    /**
     * 加载用户UserAgentInfo
     * @param req 用户请求
     * @return UserAgentInfo
     */
    UserAgentInfo load(HttpServletRequest req);
}
