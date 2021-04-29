package com.srobber.common.trace;

import com.srobber.common.config.CoreConfig;
import com.srobber.common.util.StringUtil;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 日志traceId, 用户标记关联日志记录
 *
 * @author chensenlai
 * 2020-11-24 下午2:14
 */
public class TraceContext {

    /**
     * 请求追踪traceId
     */
    private static final String TRACE_ID = "mdc_trace_id";

    private TraceContext(){}

    /**
     * 设置追踪标示符
     * 使用设备唯一码,设置MDC, 用于日志格式方便追踪请求
     * @param request 请求
     */
    public static void setTraceId(HttpServletRequest request) {
        String traceId = request.getHeader(CoreConfig.SECURITY_HEADER_DEVICE);
        if(StringUtil.isBlank(traceId)) {
            HttpSession session = request.getSession();
            if(session != null) {
                traceId = "SESSION_"+session.getId();
            } else {
                traceId = "MOCK_"+StringUtil.uuid();
            }
        }
        MDC.put(TRACE_ID, traceId);
    }

    /**
     * 清除追踪标示符号
     */
    public static void clearTraceId(HttpServletRequest request) {
        MDC.remove(TRACE_ID);
    }
}
