package com.srobber.auth.support;

import com.srobber.common.config.CoreConfig;
import com.srobber.auth.UserDeviceInfo;
import com.srobber.auth.UserDeviceInfoStore;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * Http Request作用域存储
 *
 * @author chensenlai
 * 2020-11-24 下午12:12
 */
@Slf4j
public class DefaultUserDeviceInfoStore implements UserDeviceInfoStore {

    private static final String ATTR_DEVICE_INFO = UserDeviceInfoStore.class.getName()+".device_info";

    @Override
    public UserDeviceInfo parseAndStore(HttpServletRequest req) {
        String deviceNo = req.getHeader(CoreConfig.SECURITY_HEADER_DEVICE);
        UserDeviceInfo deviceInfo = new UserDeviceInfo();
        deviceInfo.setDeviceNo(deviceNo);
        return deviceInfo;
    }

    @Override
    public UserDeviceInfo load(HttpServletRequest req) {
        Object userDeviceInfo = req.getAttribute(ATTR_DEVICE_INFO);
        if(userDeviceInfo == null) {
            return null;
        }
        return (UserDeviceInfo)userDeviceInfo;
    }
}
