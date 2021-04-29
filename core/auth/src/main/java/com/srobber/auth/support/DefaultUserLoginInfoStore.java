package com.srobber.auth.support;

import com.srobber.common.config.CoreConfig;
import com.srobber.auth.TokenCacheManager;
import com.srobber.auth.UserLoginInfo;
import com.srobber.auth.UserLoginInfoStore;
import com.srobber.common.util.StringUtil;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;

/**
 * Http Request作用域存储
 *
 * @author chensenlai
 */
public class DefaultUserLoginInfoStore implements UserLoginInfoStore {

    private static final String ATTR_LOGIN_INFO = UserLoginInfoStore.class.getName()+".login_info";

    @Setter
    private TokenCacheManager tokenCacheManager;

    @Override
    public UserLoginInfo parseAndStore(HttpServletRequest req) {
        String token = req.getHeader(CoreConfig.SECURITY_HEADER_TOKEN);
        if(StringUtil.isBlank(token)) {
            return null;
        }
        UserLoginInfo userLoginInfo = tokenCacheManager.getLoginInfo(token);
        req.setAttribute(ATTR_LOGIN_INFO, userLoginInfo);
        return userLoginInfo;
    }

    @Override
    public UserLoginInfo load(HttpServletRequest req) {
        Object userLoginInfo = req.getAttribute(ATTR_LOGIN_INFO);
        if(userLoginInfo == null) {
            return null;
        }
        return (UserLoginInfo)userLoginInfo;
    }
}
