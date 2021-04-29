package com.srobber.manager.pay.alipay.dto;

import lombok.Data;

/**
 * 支付宝ReturnUrl通知参数
 *
 * @author chensenlai
 */
@Data
public class AlipayReturnUrlDTO {

	private String appId;
	private String sellerId;
	private String tradeNo;
	private String outTradeNo;
	private int totalFee;
	private String tradeStatus;
}
