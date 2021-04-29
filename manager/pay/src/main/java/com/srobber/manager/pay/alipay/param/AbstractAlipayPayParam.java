package com.srobber.manager.pay.alipay.param;

import lombok.Data;

import java.util.Map;

/**
 * 支付宝下单参数
 *
 * @author chensenlai
 */
@Data
public abstract class AbstractAlipayPayParam {

	/**
	 * 外部订单号(必传)
	 */
	private String outTradeNo;
	/**
	 * 支付金额，单位分(必传)
	 */
	private int totalFee;
	/**
	 * 订单信息(必传)
	 */
	private String subject;
	/**
	 * 经测试，该参数无效
	 * 该笔订单允许的最晚付款时间，逾期将关闭交易。
	 * 取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
	 * 注：若为空，则默认为15d。
	 */
	@Deprecated
	private String timeoutExpress;
	/**
	 * 回传给回调函数的参数(非必传)
	 */
	private Map<String, String> passbackParams;
}
