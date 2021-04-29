package com.srobber.cache.config;

import com.srobber.cache.Cache;
import com.srobber.cache.CacheDefinition;
import com.srobber.cache.CachePrefix;
import com.srobber.cache.MultiCache;
import com.srobber.cache.support.RedisCache;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 缓存配置
 * @author chensenlai
 * 2021-04-28 下午2:32
 */
@EnableConfigurationProperties(MultiCacheProperties.class)
@Configuration
public class MultiCacheConfig {

    @Bean
    public MultiCache multiCache(MultiCacheProperties multiCacheProperties) {
        Map<String, Cache> cacheMap = new HashMap<>();
        List<MultiCacheProperties.CacheConfig> cacheConfigList = multiCacheProperties.getList();
        for(MultiCacheProperties.CacheConfig cacheConfig : cacheConfigList) {
            String key = cacheConfig.getKey();
            Cache cache = createCache(cacheConfig);
            cacheMap.put(key, cache);
        }
        MultiCache multiCache = new MultiCache();
        multiCache.setCacheMap(cacheMap);
        return multiCache;
    }

    public Cache createCache(MultiCacheProperties.CacheConfig cacheConfig) {
        return new RedisCache() {

            @Override
            public CacheDefinition getDefinition() {
                return new CacheDefinition() {
                    @Override
                    public CachePrefix getPrefix() {
                        return ()->cacheConfig.getPrefix();
                    }

                    @Override
                    public long getTimeout() {
                        return cacheConfig.getTimeout();
                    }

                    @Override
                    public TimeUnit getUnit() {
                        return cacheConfig.getUnit();
                    }

                    @Override
                    public String getRouteKey() {
                        return cacheConfig.getKey();
                    }
                };
            }
        };
    }
}
