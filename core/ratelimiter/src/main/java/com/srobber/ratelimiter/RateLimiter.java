package com.srobber.ratelimiter;

import java.lang.annotation.*;

/**
 * 分布式限流器
 *
 * @author chensenlai
 * 2020-10-23 上午11:35
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /**
     * 限流key(支持springEL, 可以通过springEL定制限流目标, 可以是IP,用户编号等)
     * @return 限流key
     */
    String key() default "ratelimiter";
    /**
     * 时间限制通过请求数
     * @return 请求次数
     */
    long limit() default 10;

    /**
     * 过期时间，单位毫秒
     * @return 过期时间
     */
    long expireMills() default 1000;

    /**
     * 流控消息提示
     * @return 提示
     */
    String message() default "请求过于频繁";
}
