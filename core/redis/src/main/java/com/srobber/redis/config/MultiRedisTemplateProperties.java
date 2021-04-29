package com.srobber.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

/**
 * 多个redis配置
 *
 * @author chensenlai
 * 2020-10-22 下午2:21
 */
@Data
@ConfigurationProperties(prefix = "srobber.redis")
public class MultiRedisTemplateProperties {

    private List<RedisConfig> list;

    @Data
    public static class RedisConfig {
        /**
         * generate spring beanName
         * {key}RedisConfig
         * {key}RedisConnectionFactory
         * {key}RedisTemplate
         */
        private String key;
        /**
         * Database index used by the connection factory.
         */
        private int database = 0;

        /**
         * Connection URL. Overrides host, port, and password. User is ignored. Example:
         * redis://user:password@example.com:6379
         */
        private String url;

        /**
         * Redis server host.
         */
        private String host = "localhost";

        /**
         * Login password of the redis server.
         */
        private String password;

        /**
         * Redis server port.
         */
        private int port = 6379;

        /**
         * Whether to enable SSL support.
         */
        private boolean ssl;

        private final Lettuce lettuce = new Lettuce();

        /**
         * Lettuce client properties.
         */
        @Data
        public static class Lettuce {

            /**
             * Shutdown timeout.
             */
            private Duration shutdownTimeout = Duration.ofMillis(100);

            /**
             * Lettuce pool configuration.
             */
            private Pool pool;
        }

        /**
         * Pool properties.
         */
        @Data
        public static class Pool {

            /**
             * Maximum number of "idle" connections in the pool. Use a negative value to
             * indicate an unlimited number of idle connections.
             */
            private int maxIdle = 8;

            /**
             * Target for the minimum number of idle connections to maintain in the pool. This
             * setting only has an effect if both it and time between eviction runs are
             * positive.
             */
            private int minIdle = 0;

            /**
             * Maximum number of connections that can be allocated by the pool at a given
             * time. Use a negative value for no limit.
             */
            private int maxActive = 8;

            /**
             * Maximum amount of time a connection allocation should block before throwing an
             * exception when the pool is exhausted. Use a negative value to block
             * indefinitely.
             */
            private Duration maxWait = Duration.ofMillis(-1);

            /**
             * Time between runs of the idle object evictor thread. When positive, the idle
             * object evictor thread starts, otherwise no idle object eviction is performed.
             */
            private Duration timeBetweenEvictionRuns;
        }
    }
}
