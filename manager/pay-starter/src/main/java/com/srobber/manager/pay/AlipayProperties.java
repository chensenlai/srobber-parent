package com.srobber.manager.pay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 支付宝支付配置属性
 *
 * @author chensenlai
 */
@Data
@ConfigurationProperties(prefix = "srobber.manager.pay.alipay")
public class AlipayProperties {
    /**
     * 支付宝支付统一开关
     */
    private boolean enabled;

    /**
     * 商户ID
     */
    private String sellerId;
    /**
     * 应用AppID
     * 开放平台申请应用AppID和权限
     * https://open.alipay.com/platform/home.htm
     */
    private String appId;
    /**
     * 应用私钥
     */
    private String appPrivateKey;
    /**
     * 支付宝公钥
     */
    private String alipayPublicKey;
    /**
     * 网页支付结果返回地址
     */
    private String returnUrl;
    /**
     * 支付宝支付结果通知回调地址
     */
    private String notifyUrl;
    /**
     * RSA2
     */
    private String signType;
}
