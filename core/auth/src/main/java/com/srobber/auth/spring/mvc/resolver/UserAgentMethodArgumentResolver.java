package com.srobber.auth.spring.mvc.resolver;


import com.srobber.auth.UserAgent;
import com.srobber.auth.UserAgentInfo;
import com.srobber.auth.UserAgentInfoStore;
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
 * UserAgent参数解析器
 *
 * @author chensenlai
 */
public class UserAgentMethodArgumentResolver implements HandlerMethodArgumentResolver, InitializingBean {

	@Setter
	private UserAgentInfoStore userAgentInfoStore;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> type = parameter.getParameterType();
		UserAgent ua = parameter.getParameterAnnotation(UserAgent.class);
		if(ua == null) {
			return false;
		}
		if (!UserAgentInfo.class.isAssignableFrom(type)) {
			throw new IllegalArgumentException("@UserAgent should be UserAgentInfo or its subclass");
		}
		return true;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
		return userAgentInfoStore.load(request);
	}

	@Override
	public void afterPropertiesSet() {
		if(this.userAgentInfoStore == null) {
			throw new IllegalStateException("can not find UserAgentInfoStore bean.");
		}
	}
}