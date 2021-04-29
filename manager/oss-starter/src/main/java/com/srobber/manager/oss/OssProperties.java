package com.srobber.manager.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OSS配置属性
 *
 * @author chensenlai
 */
@Data
@ConfigurationProperties(prefix = "srobber.manager.oss")
public class OssProperties {

    /**
     * OSS具体实现
     */
    private OssClientType type;
    /**
     * 阿里云OSS
     */
    private final Aliyun aliyun = new Aliyun();

    /**
     * 友盟推送配置
     */
    @Data
    public static class Aliyun {
        /**
         * Endpoint以杭州为例，其它Region请按实际情况填写。
         * https://oss-cn-hangzhou.aliyuncs.com
         */
        private String endpoint;
        /**
         * 阿里云账号(可以是RAM授权子帐号)
         * 服务端直接用帐号访问OSS
         * 客户端需要用STS临时授权访问OSS
         */
        private String accessKeyId;
        private String accessKeySecret;
    }
}
