package com.srobber.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srobber.common.util.JsonTypeAwareUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 类型可知的json
 *
 * @author chensenlai
 * 2020-10-22 下午3:52
 */
public class JsonTypeAwareRedisTemplate extends RedisTemplate<Object, Object> implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Jackson2JsonRedisSerializer serializer = getJackson2JsonRedisSerializer();
        //string
        this.setKeySerializer(new StringRedisSerializer());
        this.setValueSerializer(serializer);
        //hash
        this.setHashKeySerializer(new StringRedisSerializer());
        this.setHashValueSerializer(serializer);
    }

    public static Jackson2JsonRedisSerializer getJackson2JsonRedisSerializer() {
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonTypeAwareUtil.initMapper(mapper);
        serializer.setObjectMapper(mapper);
        return serializer;
    }
}
