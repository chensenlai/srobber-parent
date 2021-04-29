package com.srobber.auth.support;

import com.srobber.auth.TokenCacheManager;
import com.srobber.auth.UserLoginInfo;
import com.srobber.cache.Cache;
import com.srobber.common.spring.EnvironmentContext;
import com.srobber.common.util.StringUtil;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author chensenlai
 * 2021-04-29 下午4:42
 */
public class RemoteTokenCacheManager implements TokenCacheManager, InitializingBean {

    @Setter
    private Cache remoteTokenCache;

    @Override
    public String putLoginInfo(UserLoginInfo loginInfo) {
        String token = StringUtil.uuid();
        remoteTokenCache.set(token, loginInfo);
        return token;
    }

    @Override
    public UserLoginInfo getLoginInfo(String token) {
        return remoteTokenCache.get(token);
    }

    @Override
    public void deleteLoginInfo(String token) {
        remoteTokenCache.delete(token);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(!EnvironmentContext.isProdEnv()) {
            UserLoginInfo loginInfo = new UserLoginInfo();
            loginInfo.setUserId(10000001);
            remoteTokenCache.set("123456", loginInfo);
        }
    }
}
