package com.srobber.auth.spring.mvc.resolver;

import com.srobber.auth.UserDevice;
import com.srobber.auth.UserDeviceInfo;
import com.srobber.auth.UserDeviceInfoStore;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * UserDevice参数解析器
 * @author chensenlai
 * 2020-11-24 下午12:15
 */
public class UserDeviceMethodArgumentResolver implements HandlerMethodArgumentResolver, InitializingBean {

    @Setter
    private UserDeviceInfoStore userDeviceInfoStore;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> type = parameter.getParameterType();
        UserDevice ud = parameter.getParameterAnnotation(UserDevice.class);
        if(ud == null) {
            return false;
        }
        if (!UserDeviceInfo.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("@UserDevice should be UserDeviceInfo or its subclass");
        }
        return true;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        return userDeviceInfoStore.load(request);
    }

    @Override
    public void afterPropertiesSet() {
        if(this.userDeviceInfoStore == null) {
            throw new IllegalStateException("can not find UserDeviceInfoStore bean.");
        }
    }
}