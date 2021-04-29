package com.srobber.common.config;

import com.srobber.common.spring.EnvironmentContext;

/**
 * 系统配置管理
 *
 * @author chensenlai
 */
public class CoreConfig {

    /**
     * 枚举类包扫描起始包, 多个用,|;| | 分隔
     * ${srobber.common.enum.base-package}
     */
    public final static String ENUM_BASE_PACKAGE = EnvironmentContext.getConfigValue(
            "srobber.common.enum.base-package", String.class, "com.srobber");


    /**
     * 状态类包扫描起始包, 多个用,|;| | 分隔
     * ${srobber.common.status.base-package}
     */
    public final static String STATUS_BASE_PACKAGE = EnvironmentContext.getConfigValue(
            "srobber.common.status.base-package", String.class, "com.srobber");


    /**
     * 线程池-核心线程数
     * ${srobber.common.executor.core-pool-size}
     */
    public final static int EXECUTOR_CORE_POOL_SIZE = EnvironmentContext.getConfigValue(
            "srobber.common.executor.core-pool-size", Integer.class, Runtime.getRuntime().availableProcessors());
    /**
     * 线程池-最大线程数
     * ${srobber.common.executor.max-pool-size}
     */
    public final static int EXECUTOR_MAX_POOL_SIZE = EnvironmentContext.getConfigValue(
            "srobber.common.executor.max-pool-size", Integer.class, Runtime.getRuntime().availableProcessors());
    /**
     * 线程池-等待队列长度
     * ${srobber.common.executor.queue-capacity}
     */
    public final static int EXECUTOR_QUEUE_CAPACITY = EnvironmentContext.getConfigValue(
            "srobber.common.executor.queue-capacity", Integer.class, 200);
    /**
     * 线程池-线程idle保活时长(秒)
     * ${srobber.common.executor.keep-alive-seconds}
     */
    public final static int EXECUTOR_KEEP_ALIVE_SECONDS = EnvironmentContext.getConfigValue(
            "srobber.common.executor.keep-alive-seconds", Integer.class, 60);


    /**
     * 安全认证-自定义http User-Agent头部名
     */
    public static final String SECURITY_HEADER_USER_AGENT = EnvironmentContext.getConfigValue(
            "srobber.common.security.user-agent", String.class, "Auth-User-Agent");
    /**
     * 安全认证-自定义http Device头部名
     */
    public static final String SECURITY_HEADER_DEVICE = EnvironmentContext.getConfigValue(
            "srobber.common.security.device", String.class, "Device");
    /**
     * 安全认证-自定义http token头部名
     */
    public static final String SECURITY_HEADER_TOKEN = EnvironmentContext.getConfigValue(
            "srobber.common.security.token", String.class, "Auth-Token");

    /**
     * 安全认证-自定义接口签名key
     */
    public static final String SECURITY_KEY = EnvironmentContext.getConfigValue(
            "srobber.common.security.key", String.class, "default_key");

}
