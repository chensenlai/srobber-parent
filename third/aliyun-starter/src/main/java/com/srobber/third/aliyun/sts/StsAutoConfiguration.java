package com.srobber.third.aliyun.sts;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * STS自动配置
 *
 * @author chensenlai
 * 2020-09-24 下午3:54
 */
@Configuration
@EnableConfigurationProperties(StsProperties.class)
@ConditionalOnProperty(prefix = "srobber.third.aliyun.sts", name = "enabled", matchIfMissing = false)
public class StsAutoConfiguration {

    private final StsProperties stsProperties;

    public StsAutoConfiguration(StsProperties stsProperties) {
        this.stsProperties = stsProperties;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(name="stsThird")
    public StsThird stsThird() {
        StsThirdImpl stsThird = new StsThirdImpl();
        stsThird.setAccessKeyId(this.stsProperties.getAccessKeyId());
        stsThird.setAccessKeySecret(this.stsProperties.getAccessKeySecret());
        return stsThird;
    }
}
