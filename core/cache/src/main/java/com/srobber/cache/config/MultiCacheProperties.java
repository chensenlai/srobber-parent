package com.srobber.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存配置
 * @author chensenlai
 * 2021-04-28 下午2:32
 */
@Data
@ConfigurationProperties(prefix = "srobber.cache")
public class MultiCacheProperties {

    private List<CacheConfig> list;

    @Data
    public static class CacheConfig {
        /**
         * 缓存路由key
         */
        private String key;
        /**
         * 缓存前缀
         */
        private String prefix;
        /**
         * 超时时间 0-不超时
         */
        private long timeout;
        /**
         * 时间单位
         */
        private TimeUnit unit;
    }
}
