package com.srobber.ratelimiter.config;

import com.srobber.ratelimiter.RateLimiterAspect;
import com.srobber.ratelimiter.RateLimiterFilter;
import com.srobber.ratelimiter.support.RedisRateLimiterFilter;
import com.srobber.redis.MultiRedisTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 流控配置
 * @author chensenlai
 * 2021-04-28 下午1:55
 */
@Configuration
public class RateLimiterConfig {

    @Bean
    public RateLimiterAspect rateLimiterAspect(RateLimiterFilter rateLimiterFilter) {
        RateLimiterAspect rateLimiterAspect = new RateLimiterAspect();
        rateLimiterAspect.setRateLimiterFilter(rateLimiterFilter);
        return rateLimiterAspect;
    }

    @Bean
    @ConditionalOnMissingBean(name = "rateLimiterFilter")
    public RateLimiterFilter rateLimiterFilter(MultiRedisTemplate multiRedisTemplate) {
        RedisRateLimiterFilter rateLimiterFilter = new RedisRateLimiterFilter();
        rateLimiterFilter.setMultiRedisTemplate(multiRedisTemplate);
        rateLimiterFilter.init();
        return rateLimiterFilter;
    }
}
