package com.srobber.manager.pay.wxpay.dto;

import lombok.Data;

/**
 * 微信支付请求退款返回数据
 *
 * @author chensenlai
 */
@Data
public class WxpayRefundDTO {

	private String transactionId;
	private String outTradeNo;
	private String outRefundNo;
	private String refundId;
	private String refundFee;
	private String totalFee;
}
