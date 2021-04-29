package com.srobber.manager.pay.wxpay.param;

import lombok.Data;

/**
 * 微信公扫码支付请求统一下单参数
 *
 * @author chensenlai
 */
@Data
public class WxpayNativePayParam extends AbstractWxpayPayParam {

	private String productId;
}
