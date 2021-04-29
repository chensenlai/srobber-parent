package com.srobber.cache;

import java.util.concurrent.TimeUnit;

/**
 * 缓存配置定义
 *
 * @author chensenlai
 * 2020-09-18 上午11:18
 */
public interface CacheDefinition {

    /**
     * 缓存前缀
     * @return 缓存前缀
     */
    CachePrefix getPrefix();

    /**
     * 缓存过期时间
     * @return 超时时间
     */
    default long getTimeout() {
        return 0;
    };

    /**
     * 缓存超时时间单位
     * @return 时间单位
     */
    default TimeUnit getUnit() {
        return TimeUnit.SECONDS;
    };

    /**
     * 缓存key, 用于路由到不同的缓存
     * @return 缓存key
     */
    default String getRouteKey() {
        return "default";
    }
}
