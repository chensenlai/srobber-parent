package com.srobber.manager.pay.wxpay.param;

import lombok.Data;

/**
 * 微信小程序请求统一下单参数
 *
 * @author chensenlai
 */
@Data
public class WxpayProgramPayParam extends AbstractWxpayPayParam {
	
	private String openid;
}
