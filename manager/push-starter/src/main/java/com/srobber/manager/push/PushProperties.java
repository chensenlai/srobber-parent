package com.srobber.manager.push;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 推送配置属性
 *
 * @author chensenlai
 */
@Data
@ConfigurationProperties(prefix = "srobber.manager.push")
public class PushProperties {
    /**
     * 推送具体实现
     */
    private PushClientType type;
    /**
     * 友盟推送
     */
    private final Umeng umeng = new Umeng();
    /**
     * 信鸽推送
     */
    private final Xinge xinge = new Xinge();
    /**
     * 极光推送
     */
    private final Jpush jpush = new Jpush();

    /**
     * 友盟推送配置
     */
    @Data
    public static class Umeng {
        private String androidAppkey;
        private String androidAppMasterSecret;
        private String iosAppkey;
        private String iosAppMasterSecret;
    }
    /**
     * 信鸽推送配置
     */
    @Data
    public static class Xinge {
        private String androidAppId;
        private String androidSecretKey;
        private String iosAppId;
        private String iosSecretKey;
    }

    /**
     * 极光推送配置
     */
    @Data
    public static class Jpush {
        private String masterSecret;
        private String appKey;
    }
}
