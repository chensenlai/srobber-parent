package com.srobber.manager.pay.alipay.dto;

import lombok.Data;

/**
 * 付宝NotifyUrl通知参数
 *
 * @author chensenlai
 */
@Data
public class AlipayNotifyUrlDTO {

	private String appId;
	private String sellerId;
	private String tradeNo;
	private String outTradeNo;
	private int totalFee;
	private String tradeStatus;
}
