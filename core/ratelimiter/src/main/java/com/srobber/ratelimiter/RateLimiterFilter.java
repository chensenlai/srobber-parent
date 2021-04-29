package com.srobber.ratelimiter;

/**
 * 流控处理器
 *
 * @author chensenlai
 * 2020-10-23 下午12:01
 */
public interface RateLimiterFilter {

    /**
     * 流控过滤器
     * @param limitKey 流控key(标识具体流控对象)
     * @param limitTimes 允许次数
     * @param limitExpireMills 单位过期时间
     * @return 是否通过
     */
    boolean doFilter(String limitKey, long limitTimes, long limitExpireMills);
}
