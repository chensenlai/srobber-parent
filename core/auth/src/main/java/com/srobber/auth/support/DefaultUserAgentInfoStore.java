package com.srobber.auth.support;

import com.srobber.common.config.CoreConfig;
import com.srobber.auth.UserAgentInfo;
import com.srobber.auth.UserAgentInfoStore;
import com.srobber.common.util.StringUtil;
import com.srobber.common.util.WebUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * Http Request作用域存储
 *
 * @author chensenlai
 */
@Slf4j
public class DefaultUserAgentInfoStore implements UserAgentInfoStore {

    private static final String ATTR_USERAGENT_INFO = UserAgentInfoStore.class.getName()+".userAgent_info";
    private static final String SPLIT = "#";

    @Override
    public UserAgentInfo parseAndSore(HttpServletRequest req) {
        String userAgent = req.getHeader(CoreConfig.SECURITY_HEADER_USER_AGENT);
        if(userAgent == null
                || userAgent.split(SPLIT).length != 5) {
            log.warn("UserAgent error, {}", userAgent);
            return null;
        }
        String[] userAgentArr = userAgent.split(SPLIT);
        String os = userAgentArr[0];
        String market = userAgentArr[1];
        String isp = userAgentArr[2];
        String client = userAgentArr[3];
        int appVersion = 0;
        try{
            appVersion = Integer.valueOf(userAgentArr[4]);
        } catch(Exception e) {
            log.warn("UserAgent error, UA {}", userAgent);
            return null;
        }
        String ip = WebUtil.getClientIp(req);
        if(StringUtil.isBlank(ip)) {
            ip = "0.0.0.0";
        }
        UserAgentInfo userAgentInfo = new UserAgentInfo();
        userAgentInfo.setOs(os);
        userAgentInfo.setMarket(market);
        userAgentInfo.setIsp(isp);
        userAgentInfo.setClient(client);
        userAgentInfo.setAppVersion(appVersion);
        userAgentInfo.setIp(ip);
        req.setAttribute(ATTR_USERAGENT_INFO, userAgentInfo);
        return userAgentInfo;
    }

    @Override
    public UserAgentInfo load(HttpServletRequest req) {
        Object userAgentInfo = req.getAttribute(ATTR_USERAGENT_INFO);
        if(userAgentInfo == null) {
            return null;
        }
        return (UserAgentInfo)userAgentInfo;
    }
}
