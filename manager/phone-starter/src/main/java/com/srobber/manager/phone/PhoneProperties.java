package com.srobber.manager.phone;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 号码配置属性
 *
 * @author chensenlai
 */
@Data
@ConfigurationProperties(prefix = "srobber.manager.phone")
public class PhoneProperties {
    /**
     * 号码具体实现
     */
    private PhoneClientType type;
    /**
     * 阿里云
     */
    private final Aliyun aliyun = new Aliyun();
    /**
     * 闪验
     */
    private final Shanyan shanyan = new Shanyan();

    /**
     * 阿里云号码配置
     */
    @Data
    public static class Aliyun {
        private String regionId;
        private String accessKeyId;
        private String accessSecret;
    }
    /**
     * 闪验号码配置
     */
    @Data
    public static class Shanyan {
        private String appId;
        private String appKey;
        private String encryptType = "0";
        private String privateKey = "";
    }
}
