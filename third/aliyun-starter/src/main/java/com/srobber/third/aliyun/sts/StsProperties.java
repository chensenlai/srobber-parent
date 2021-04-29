package com.srobber.third.aliyun.sts;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * STS参数配置
 *
 * @author chensenlai
 * 2020-09-24 下午3:55
 */
@Data
@ConfigurationProperties(prefix = "srobber.third.aliyun.sts")
public class StsProperties {

    /**
     * 是否启用阿里云STS配置
     */
    private Boolean enabled;
    /**
     * assumeRole权限用户accessKeyId
     */
    private String accessKeyId;
    /**
     * assumeRole权限用户accessKeyId
     */
    private String accessKeySecret;
}
