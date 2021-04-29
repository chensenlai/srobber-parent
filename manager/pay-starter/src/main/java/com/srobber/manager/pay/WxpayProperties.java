package com.srobber.manager.pay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信支付配置属性
 *
 * @author chensenlai
 */
@Data
@ConfigurationProperties(prefix = "srobber.manager.pay.wxpay")
public class WxpayProperties {
    /**
     * 微信支付统一开关
     */
    private boolean enabled;

    /**
     * 微信支付商户号 PartnerID 通过微信支付商户资料审核后邮件发送
     * https://pay.weixin.qq.com
     */
    private String mchId;
    /**
     * 管理中心-移动应用
     * https://open.weixin.qq.com/
     */
    private String appId;
    /**
     * 帐户设置-安全设置-API安全-API密钥-设置API密钥
     * https://pay.weixin.qq.com
     */
    private String key;
    /**
     * 微信支付结果回调通知地址
     */
    private String notifyUrl;
    /**
     * MD5/HMACSHA256
     */
    private String signType;
}
