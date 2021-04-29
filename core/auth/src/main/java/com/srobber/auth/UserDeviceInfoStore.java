package com.srobber.auth;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户设备信息存储管理
 *
 * @author chensenlai
 * 2020-11-24 下午12:08
 */
public interface UserDeviceInfoStore {

    /**
     * 解析和存储用户UserDeviceInfo
     * @param req 用户请求
     * @return UserDeviceInfo
     */
    UserDeviceInfo parseAndStore(HttpServletRequest req);

    /**
     * 加载用户UserDeviceInfo
     * @param req 用户请求
     * @return UserDeviceInfo
     */
    UserDeviceInfo load(HttpServletRequest req);
}
