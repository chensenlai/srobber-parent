package com.srobber.cache.config;

import com.srobber.redis.JsonTypeAwareRedisTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Spring Cache配置 (Redis CacheManager自定义配置)
 * 1 事务感知
 * 2 使用自定义json类型序列化
 * 3 cache支持统一prefixkey
 *
 * @author chensenlai
 */
@Slf4j
@Configuration
public class SpringCacheConfig implements CacheManagerCustomizer {

    @Autowired
    private CacheProperties cacheProperties;

    @Override
    public void customize(CacheManager cacheManager) {
        //redis缓存感知事务存在， 等事务提交再刷缓存
        if(cacheManager instanceof RedisCacheManager) {
            RedisCacheManager redisCacheManager = (RedisCacheManager) cacheManager;
            redisCacheManager.setTransactionAware(true);
        }

        //反射增强初始化@Cacheable支持配置个性化过期时间
        if(cacheManager instanceof RedisCacheManager) {
            RedisCacheManager redisCacheManager = (RedisCacheManager) cacheManager;
            try {
                Field field = RedisCacheManager.class.getDeclaredField("initialCacheConfiguration");
                boolean oldAccess = field.isAccessible();
                if(!oldAccess) {
                    field.setAccessible(true);
                }
                Map<String, RedisCacheConfiguration> initialCacheConfiguration = (Map<String, RedisCacheConfiguration>)field.get(redisCacheManager);
                field.set(redisCacheManager, initialCacheConfiguration);
                if(!oldAccess) {
                    field.setAccessible(oldAccess);
                }
            } catch (Exception e) {
                log.warn("RedisCacheManager @CacheTTL weave 'initialCacheConfiguration' error, {}", e.getMessage());
            }
        }
    }

    /**
     * Spring Cache使用这个RedisTemplate
     * 1. string类型使用string序列化
     * 2. hash类型使用自定义json类型序列化(类型可感知)
     * @param redisConnectionFactory 具体实现连接工厂
     * @return RedisTemplate实例
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        JsonTypeAwareRedisTemplate redisTemplate = new JsonTypeAwareRedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public RedisCacheConfiguration redisConfiguration() {
        CacheProperties.Redis redisProperties = this.cacheProperties.getRedis();
        RedisCacheConfiguration config = RedisCacheConfiguration
                .defaultCacheConfig();

        Jackson2JsonRedisSerializer serializer = JsonTypeAwareRedisTemplate.getJackson2JsonRedisSerializer();
        config = config.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(serializer));
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        //spring cache前缀
        if (redisProperties.getKeyPrefix() != null) {
            config = config.computePrefixWith((cacheName -> {return redisProperties.getKeyPrefix()+"::"+cacheName+"::";}));
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }
}
