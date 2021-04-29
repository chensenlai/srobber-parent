package com.srobber.manager.pay.wxpay.param;

import lombok.Data;

/**
 * 微信公众号上页面请求统一下单参数
 *
 * @author chensenlai
 */
@Data
public class WxpayMpPayParam extends AbstractWxpayPayParam {

	/**
	 * trade_type=JSAPI时（即JSAPI支付），此参数必传，此参数为微信用户在商户对应appid下的唯一标识
	 */
	private String openid;
}
