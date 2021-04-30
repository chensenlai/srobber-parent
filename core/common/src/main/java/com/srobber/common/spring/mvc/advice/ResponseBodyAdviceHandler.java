package com.srobber.common.spring.mvc.advice;

import com.srobber.common.result.JsonResult;
import com.srobber.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;

/**
 * 响应拦截
 *
 * @author chensenlai
 */
@Slf4j
@ControllerAdvice
public class ResponseBodyAdviceHandler implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        Method method = returnType.getMethod();
        if(method != null) {
            if(method.getReturnType().isAssignableFrom(JsonResult.class)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if(body instanceof JsonResult) {
            if(log.isDebugEnabled()) {
                String method = request.getMethod().name();
                String uri = request.getURI().toString();
                log.debug("{} {} {}", uri, method, JsonUtil.toStr(body));
            }
        }
        return body;
    }
}
