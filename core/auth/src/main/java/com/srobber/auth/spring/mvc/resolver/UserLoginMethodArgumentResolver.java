package com.srobber.auth.spring.mvc.resolver;

import com.srobber.auth.UserLogin;
import com.srobber.auth.UserLoginInfo;
import com.srobber.auth.UserLoginInfoStore;
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
 * UserLogin数解析器
 *
 * @author chensenlai
 **/
public class UserLoginMethodArgumentResolver implements HandlerMethodArgumentResolver, InitializingBean {

    @Setter
    private UserLoginInfoStore userLoginInfoStore;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> type = parameter.getParameterType();
        UserLogin ul = parameter.getParameterAnnotation(UserLogin.class);
        if(ul == null) {
            return false;
        }
        if (!UserLoginInfo.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("@UserLogin should be UserLoginInfo or its subclass");
        }
        return true;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory){
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        return userLoginInfoStore.load(request);
    }

    @Override
    public void afterPropertiesSet() {
        if(this.userLoginInfoStore == null) {
            throw new IllegalStateException("can not find UserLoginInfoStore bean.");
        }
    }
}