package com.srobber.common.spring.mvc.interceptor;

import com.srobber.common.config.CoreConfig;
import com.srobber.common.trace.TraceContext;
import com.srobber.common.util.StringUtil;
import com.srobber.common.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 执行拦截器
 *
 * @author chensenlai
 */
@Slf4j
public class ExecuteInterceptor extends HandlerInterceptorAdapter {

    /**
     * 请求作用域属性名
     */
    private static final String CT_ATTR = ".ct_time";
    /**
     * 消耗耗时阀值
     */
    private final static int SPENT_MILLS = 500;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        TraceContext.setTraceId(request);
        request.setAttribute(CT_ATTR, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

        long cost = 0;
        Object time = request.getAttribute(CT_ATTR);
        if(time != null) {
            cost = System.currentTimeMillis() - Long.parseLong(time.toString());
            request.removeAttribute(CT_ATTR);
        }

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String params = WebUtil.getParameterStr(WebUtil.getParameterMap(request));
        if(cost > SPENT_MILLS) {
            log.warn("{} {} params={} cost={}ms", uri, method, params, cost);
        } else {
            log.info("{} {} params={} cost={}ms", uri, method, params, cost);
        }
        TraceContext.clearTraceId(request);
    }
}
