package com.srobber.manager.oss;

import com.srobber.manager.oss.aliyun.AliyunOssManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * OSS自动配置
 *
 * @author chensenlai
 */
@Configuration
@EnableConfigurationProperties(OssProperties.class)
public class OssAutoConfiguration {

    @Bean
    @Primary
    @ConditionalOnMissingBean(name="ossManager")
    @ConditionalOnProperty(prefix = "srobber.manager.oss", name = "type", havingValue = "aliyun", matchIfMissing = true)
    public OssManager aliyunOssManager(OssProperties ossProperties) {
        OssProperties.Aliyun aliyun = ossProperties.getAliyun();
        AliyunOssManager ossManager = new AliyunOssManager();
        ossManager.setEndpoint(aliyun.getEndpoint());
        ossManager.setAccessKeyId(aliyun.getAccessKeyId());
        ossManager.setAccessKeySecret(aliyun.getAccessKeySecret());
        return ossManager;
    }
}
