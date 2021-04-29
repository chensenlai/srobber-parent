package com.srobber.cache.support;

import com.srobber.cache.Cache;
import com.srobber.cache.CacheDefinition;
import com.srobber.cache.CachePrefix;
import com.srobber.common.util.StringUtil;
import com.srobber.redis.MultiRedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 缓存基类
 *
 * @author chensenlai
 * 2020-09-18 下午12:42
 */
public abstract class RedisCache implements Cache {

    @Autowired
    private MultiRedisTemplate multiRedisTemplate;

    @Override
    public <T> T get(String key) {
        CacheDefinition definition = getDefinition();
        String routeKey = definition.getRouteKey();
        CachePrefix prefix = definition.getPrefix();
        RedisTemplate<Object, Object> redisTemplate = multiRedisTemplate.choose(routeKey);
        String realKey = buildKey(prefix, key);
        Object value = redisTemplate.opsForValue().get(realKey);
        return (T)value;
    }

    @Override
    public <T> void set(String key, T value) {
        CacheDefinition definition = getDefinition();
        String routeKey = definition.getRouteKey();
        CachePrefix prefix = definition.getPrefix();
        long timeout = definition.getTimeout();
        TimeUnit unit = definition.getUnit();
        RedisTemplate<Object, Object> redisTemplate = multiRedisTemplate.choose(routeKey);
        String realKey = buildKey(prefix, key);
        if(timeout < 1) {
            redisTemplate.opsForValue().set(realKey, value);
        } else {
            redisTemplate.opsForValue().set(realKey, value, timeout, unit);
        }
    }

    @Override
    public void delete(String key) {
        CacheDefinition definition = getDefinition();
        String routeKey = definition.getRouteKey();
        CachePrefix prefix = definition.getPrefix();
        RedisTemplate<Object, Object> redisTemplate = multiRedisTemplate.choose(routeKey);
        String realKey = buildKey(prefix, key);
        redisTemplate.delete(realKey);
    }

    private String buildKey(CachePrefix prefix, String key) {
        if(prefix==null || StringUtil.isBlank(prefix.name())) {
            return key;
        }
        return prefix.name()+"::"+key;
    }
}
