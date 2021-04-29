package com.srobber.manager.pay.wxpay.param;

import lombok.Data;

import java.util.Map;

/**
 * 微信支付请求公用参数
 *
 * @author chensenlai
 */
@Data
public class AbstractWxpayPayParam {

	/**
	 * 商品描述(必传)
	 */
	private String body;
	/**
	 * 商户订单号(必传)
	 */
	private String outTradeNo;
	/**
	 * 总金额，单位分(必传)
	 */
	private int totalFee;
	/**
	 * 终端IP(必传),支持IPV4和IPV6两种格式的IP地址。调用微信支付API的机器IP
	 */
	private String spbillClientIp;
	//订单失效时间是针对订单号而言的，
	//由于在请求支付的时候有一个必传参数prepay_id只有两小时的有效期，
	//所以在重入时间超过2小时的时候需要重新请求下单接口获取新的prepay_id。
	/**
	 * 交易起始时间，订单生成时间，格式为yyyyMMddHHmmss
	 */
	private String timeStart;
	/**
	 * 交易结束时间，订单失效时间，格式为yyyyMMddHHmmss
	 */
	private String timeExpire;
	/**
	 * 附加数据，在查询API和支付通知中原样返回
	 */
	private Map<String, String> attach;
}
