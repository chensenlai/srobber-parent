package com.srobber.manager.pay.wxpay.param;

import lombok.Data;

/**
 * 微信wap(H5网页)请求统一下单参数
 *
 * @author chensenlai
 */
@Data
public class WxpayH5PayParam extends AbstractWxpayPayParam {

	/**
	 * 支付完后返回页面
	 */
	private String redirectUrl;
}
