package com.srobber.manager.pay.wxpay.dto;

import lombok.Data;

/**
 * 微信支付交易查询返回数据
 *
 * @author chensenlai
 */
@Data
public class WxpayQueryDTO {

	private String transactionId;
	private String outTradeNo;
	private String totalFee;
	private String tradeState;
}
