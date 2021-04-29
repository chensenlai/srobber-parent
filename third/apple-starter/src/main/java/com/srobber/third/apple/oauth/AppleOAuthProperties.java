package com.srobber.third.apple.oauth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 苹果帐号配置属性
 *
 * @author chensenlai
 * 2020-09-30 下午2:18
 */
@Data
@ConfigurationProperties(prefix = "srobber.third.apple.oauth")
public class AppleOAuthProperties {

    /**
     * 是否启用苹果OAuth配置
     */
    private Boolean enabled;
}
