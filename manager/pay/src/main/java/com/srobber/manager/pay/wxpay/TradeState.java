package com.srobber.manager.pay.wxpay;

/**
 * 微信支付状态
 *
 * @author chensenlai
 */
public interface TradeState {

	/**
	 * 支付成功
	 */
	String SUCCESS = "SUCCESS";
	/**
	 * 转入退款
	 */
	String REFUND = "REFUND";
	/**
	 * 未支付
	 */
	String NOTPAY = "NOTPAY";
	/**
	 * 已关闭
	 */
	String CLOSED = "CLOSED";
	/**
	 * 已扯销
	 */
	String REVOKED = "REVOKED";
	/**
	 * 用户支付中
	 */
	String USERPAYING = "USERPAYING";
	/**
	 * 支付失败
	 */
	String PAYERROR = "PAYERROR";
}
