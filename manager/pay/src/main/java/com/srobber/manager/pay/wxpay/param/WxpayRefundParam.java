package com.srobber.manager.pay.wxpay.param;

import lombok.Data;

/**
 * 微信支付请求退款参数
 *
 * @author chensenlai
 */
@Data
public class WxpayRefundParam {

	private String outRefundNo;
	private String transactionId;
	private String outTradeNo;
	private String totalFee;
	private String refundFee;
}
