package com.srobber.common.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

import java.util.Arrays;

/**
 * 环境上下文信息
 *
 * 注意: 为解决在其他Bean创建时能获取环境信息(env),
 * 不使用EnvironmentAware织入Environment, Environment是在Bean创建之后织入.
 * 使用实现SpringApplicationRunListener, 因为SpringBoot启动时会先把环境发到RunListener.
 * META-INF/spring.factories配置该RunListener
 *
 * @author chensenlai
 * 2020-09-24 下午2:37
 */
@Slf4j
public class EnvironmentContext implements SpringApplicationRunListener, Ordered {

    private static Environment env;

    public EnvironmentContext(SpringApplication application, String[] args) {
        //ignore
    }

    /**
     * 根据profiles是否生产环境
     * @return 返回是否生产环境
     */
    public static boolean isProdEnv() {
        checkAndInitEnvironment();
        String[] profiles = env.getActiveProfiles();
        return Arrays.stream(profiles)
                .anyMatch(profile->"prod".equalsIgnoreCase(profile) || "product".endsWith(profile));
    }

    /**
     * 获取应用配置properties或yml配置参数值
     * @param key 配置key
     * @param targetType 配置值类型
     * @param <T> 泛型
     * @return 配置value
     */
    public static <T> T getConfigValue(String key, Class<T> targetType) {
        checkAndInitEnvironment();
        return getConfigValue(key, targetType, null);
    }

    /**
     * 获取应用配置properties或yml配置参数值, 不存在则返回默认值
     * @param key 配置key
     * @param targetType 配置值类型
     * @param defaultValue 默认值
     * @param <T> 泛型
     * @return 配置value
     */
    public static <T> T getConfigValue(String key, Class<T> targetType, T defaultValue) {
        checkAndInitEnvironment();
        if(!env.containsProperty(key)) {
            return defaultValue;
        }
        return env.getProperty(key, targetType);
    }

    private static void checkAndInitEnvironment() {
        if(env == null) {
            log.warn("!!! Environment not in springboot, fallback to use standard");
            env = new StandardEnvironment();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
