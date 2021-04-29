package com.srobber.manager.pay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 苹果支付配置属性
 *
 * @author chensenlai
 * 2020-10-12 下午4:00
 */
@Data
@ConfigurationProperties(prefix = "srobber.manager.pay.apple")
public class ApplePayProperties {

    /**
     * 微信支付统一开关
     */
    private boolean enabled;

    /**
     * 苹果支付密码
     */
    private String password;
}
