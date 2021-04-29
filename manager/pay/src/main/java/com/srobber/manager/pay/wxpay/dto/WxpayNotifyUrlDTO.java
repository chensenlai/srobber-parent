package com.srobber.manager.pay.wxpay.dto;

import lombok.Data;

/**
 * 微信支付回调NotifyUrl通知返回数据
 *
 * @author chensenlai
 */
@Data
public class WxpayNotifyUrlDTO {

	/**
	 * 应用ID
	 */
	private String appid;
	/**
	 * 商户号
	 */
	private String mchId;
	/**
	 * 订单金额(单位分)
	 */
	private int totalFee;
	/**
	 * 现金支付金额(单位分)
	 * TODO 用户实际支付金额？
	 */
	private int cashFee;
	/**
	 * 微信支付订单号
	 */
	private String transactionId;
	/**
	 * 商户订单号
	 */
	private String outTradeNo;
}
