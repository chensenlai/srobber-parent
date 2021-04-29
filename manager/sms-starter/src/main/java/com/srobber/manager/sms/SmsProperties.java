package com.srobber.manager.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 短信配置属性
 *
 * @author chensenlai
 */
@Data
@ConfigurationProperties(prefix = "srobber.manager.sms")
public class SmsProperties {
    /**
     * 短信客户端类型
     */
    private SmsClientType type;
    /**
     * 阿里云短信
     */
    private final Aliyun aliyun = new Aliyun();
    /**
     * 玄武短信
     */
    private final Xuanwu xuanwu = new Xuanwu();

    /**
     * 阿里云短信配置
     */
    @Data
    public static class Aliyun {
        private String accessKeyId;
        private String accessSecret;
        private String signName;
    }

    /**
     * 玄武短信配置
     */
    @Data
    public static class Xuanwu {
        private String user;
        private String psw;
        private String ip;
        private int mtPort;
        private int moPort;
    }
}
