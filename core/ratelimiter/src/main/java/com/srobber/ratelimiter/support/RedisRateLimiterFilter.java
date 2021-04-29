package com.srobber.ratelimiter.support;

import com.srobber.ratelimiter.RateLimiterFilter;
import com.srobber.redis.MultiRedisTemplate;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

/**
 * Redis实现分布式流控过滤器
 *
 * @author chensenlai
 * 2020-10-23 下午12:06
 */
@Slf4j
public class RedisRateLimiterFilter implements RateLimiterFilter {

    @Setter
    private MultiRedisTemplate multiRedisTemplate;

    private DefaultRedisScript<Long> rateLimiterLuaScript;

    public void init() {
        rateLimiterLuaScript = new DefaultRedisScript<>();
        rateLimiterLuaScript.setResultType(Long.class);
        rateLimiterLuaScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("rateLimiter.lua")));
        log.info("RedisRateLimiterFilter lua script loaded");
    }

    @Override
    public boolean doFilter(String limitKey, long limitTimes, long limitExpireMills) {
        Long result = (Long) multiRedisTemplate.choose("ratelimiter").execute(rateLimiterLuaScript,
                Arrays.asList(limitKey),
                limitExpireMills, limitTimes);
        if (result == 0) {
            return false;
        }
        return true;
    }
}
