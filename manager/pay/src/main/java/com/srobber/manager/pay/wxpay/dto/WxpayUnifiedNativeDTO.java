package com.srobber.manager.pay.wxpay.dto;

import lombok.Data;

/**
 * 微信扫码支付统一下单返回数据
 *
 * @author chensenlai
 */
@Data
public class WxpayUnifiedNativeDTO {

	private String codeUrl;
}
