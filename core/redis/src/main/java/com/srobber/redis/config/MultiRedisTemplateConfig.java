package com.srobber.redis.config;

import com.srobber.common.util.StringUtil;
import com.srobber.redis.JsonTypeAwareRedisTemplate;
import com.srobber.redis.MultiRedisTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多个RedisTemplate配置
 * 不同业务可以走不同redis实例
 *
 * @author chensenlai
 * 2020-10-22 下午3:34
 */
@Slf4j
@EnableConfigurationProperties(MultiRedisTemplateProperties.class)
@Configuration
public class MultiRedisTemplateConfig {

    @Bean
    public MultiRedisTemplate multiRedisTemplate(MultiRedisTemplateProperties multiRedisTemplateProperties) {
        Map<String, RedisTemplate<Object, Object>> redisTemplateMap = new HashMap<>();
        List<MultiRedisTemplateProperties.RedisConfig> redisConfigList = multiRedisTemplateProperties.getList();
        for(MultiRedisTemplateProperties.RedisConfig redisConfig : redisConfigList) {
            String key = redisConfig.getKey();
            RedisTemplate<Object, Object> redisTemplate = createRedisTemplate(redisConfig);
            redisTemplateMap.put(key, redisTemplate);
        }
        MultiRedisTemplate multiRedisTemplate = new MultiRedisTemplate();
        multiRedisTemplate.setRedisTemplateMap(redisTemplateMap);
        return multiRedisTemplate;
    }

    /**
     * 根据redis配置构建RedisTemplate
     * @param redisConfig redis配置
     * @return RedisTemplate实例
     */
    public RedisTemplate<Object, Object> createRedisTemplate(MultiRedisTemplateProperties.RedisConfig redisConfig) {
        JsonTypeAwareRedisTemplate redisTemplate = new JsonTypeAwareRedisTemplate();
        RedisConnectionFactory redisConnectionFactory = createConnectionFactory(redisConfig);
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    public RedisConnectionFactory createConnectionFactory(MultiRedisTemplateProperties.RedisConfig redisConfig) {
        RedisStandaloneConfiguration redisStandaloneConfig = createRedisConfig(redisConfig);
        GenericObjectPoolConfig poolConfig = createPoolConfig(redisConfig);

        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();
        builder.poolConfig(poolConfig);
        builder.clientName(redisConfig.getKey());
        if(redisConfig.isSsl()) {
            builder.useSsl();
        }
        if(redisConfig.getLettuce() != null) {
            MultiRedisTemplateProperties.RedisConfig.Lettuce lettuce = redisConfig.getLettuce();
            if(lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(lettuce.getShutdownTimeout());
            }
        }

        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisStandaloneConfig, builder.build());
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }


    public RedisStandaloneConfiguration createRedisConfig(MultiRedisTemplateProperties.RedisConfig redisConfig) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisConfig.getHost());
        if(StringUtil.isBlank(redisConfig.getPassword())) {
            config.setPassword(RedisPassword.none());
        } else {
            config.setPassword(RedisPassword.of(redisConfig.getPassword()));
        }
        config.setPort(redisConfig.getPort());
        config.setDatabase(redisConfig.getDatabase());
        return config;
    }

    private GenericObjectPoolConfig createPoolConfig(MultiRedisTemplateProperties.RedisConfig redisConfig) {
        MultiRedisTemplateProperties.RedisConfig.Pool pool = redisConfig.getLettuce().getPool();
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(pool.getMaxActive());
        config.setMaxIdle(pool.getMaxIdle());
        config.setMinIdle(pool.getMinIdle());
        config.setMaxWaitMillis(pool.getMaxWait().toMillis());
        if(pool.getTimeBetweenEvictionRuns() != null) {
            config.setTimeBetweenEvictionRunsMillis(pool.getTimeBetweenEvictionRuns().toMillis());
        }
        return config;
    }
}
