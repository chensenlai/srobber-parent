package com.srobber.redis;

import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

/**
 * 多个RedisTemplate, 使用时切换
 *
 * @author chensenlai
 * 2020-10-22 下午3:19
 */
@Setter
public class MultiRedisTemplate {

    private Map<String, RedisTemplate<Object, Object>> redisTemplateMap;

    public RedisTemplate choose(String lookupKey) {
        RedisTemplate redisTemplate = redisTemplateMap.get(lookupKey);
        if(redisTemplate == null) {
            throw new IllegalArgumentException("RedisTemplate "+lookupKey+" not found");
        }
        return redisTemplate;
    }
}