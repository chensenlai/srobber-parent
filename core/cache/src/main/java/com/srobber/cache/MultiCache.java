package com.srobber.cache;

import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

/**
 * @author chensenlai
 * 2021-04-28 下午2:57
 */
public class MultiCache {

    @Setter
    private Map<String, Cache> cacheMap;

    public Cache choose(String lookupKey) {
        Cache cache = cacheMap.get(lookupKey);
        if(cache == null) {
            throw new IllegalArgumentException("Cache "+lookupKey+" not found");
        }
        return cache;
    }
}
