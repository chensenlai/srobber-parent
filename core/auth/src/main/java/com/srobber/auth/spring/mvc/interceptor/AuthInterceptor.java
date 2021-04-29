package com.srobber.auth.spring.mvc.interceptor;

import com.srobber.auth.*;
import com.srobber.common.config.CoreConfig;
import com.srobber.common.exeption.StatusHolderException;
import com.srobber.common.status.CommonStatus;
import com.srobber.common.util.SignatureUtil;
import com.srobber.common.util.StringUtil;
import com.srobber.common.util.WebUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 用户身份认证拦截器
 *
 * @author chensenlai
 */
@Slf4j
public class AuthInterceptor extends HandlerInterceptorAdapter implements InitializingBean {

    @Setter
    private UserAgentInfoStore userAgentInfoStore;
    @Setter
    private UserLoginInfoStore userLoginInfoStore;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        //跨域请求不拦截
        if(CorsUtils.isPreFlightRequest(request)) {
            return true;
        }
        //测试环境(swagger)
        if(request.getRequestURI().contains("swagger")) {
            return true;
        }

        //UserAgent验证
        UserAgentInfo userAgentInfo = userAgentInfoStore.parseAndSore(request);
        if(userAgentInfo == null) {
            throw new StatusHolderException(CommonStatus.ParamInvalid, "UA参数异常");
        }

        //接口登陆验证
        if(handler instanceof HandlerMethod) {
            UserLoginInfo userLoginInfo = userLoginInfoStore.parseAndStore(request);
            Method executeMethod = ((HandlerMethod) handler).getMethod();
            boolean needAuth = executeMethod.isAnnotationPresent(Authentication.class);
            if(needAuth) {
                if(userLoginInfo == null) {
                    throw new StatusHolderException(CommonStatus.NoAuthenticate);
                }
            }
        }

        //接口签名验证
        if(userAgentInfo.isAndroid() || userAgentInfo.isiOS()) {
            //Android或iOS, 检查签名
            String signName = SignatureUtil.SIGN;
            String timestampName = SignatureUtil.TIMESTAMP;

            String signKey = CoreConfig.SECURITY_KEY;

            String sign = request.getParameter(signName);
            String timestamp = request.getParameter(timestampName);
            if(StringUtil.isBlank(sign) || StringUtil.isBlank(timestamp)) {
                throw new StatusHolderException(CommonStatus.SignFail, "缺少签名参数");
            }
            Map<String, String> paramMap = WebUtil.getParameterMap(request);
            String mySign = SignatureUtil.genSignature(paramMap, signKey);
            if(!mySign.equals(sign)) {
                throw new StatusHolderException(CommonStatus.SignFail);
            }
        }
        return true;
    }

    @Override
    public void afterPropertiesSet() {
        if(this.userAgentInfoStore == null) {
            throw new IllegalStateException("can not find UserAgentInfoStore bean.");
        }
        if(this.userLoginInfoStore == null) {
            throw new IllegalStateException("can not find UserLoginInfoStore bean.");
        }
    }
}
