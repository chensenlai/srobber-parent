package com.srobber.manager.pay.alipay;

/**
 * 交易状态
 *
 * @author chensenlai
 */
public interface TradeStatus {
	/**
	 * 交易创建，等待买家付款。
	 */
	String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
	/**
	 * 在指定时间段内未支付时关闭的交易；在交易完成全额退款成功时关闭的交易。
	 */
	String TRADE_CLOSED = "TRADE_CLOSED";
	/**
	 * 交易成功，且可对该交易做操作，如：多级分润、退款等。
	 */
	String TRADE_SUCCESS = "TRADE_SUCCESS";
	/**
	 * 交易成功且结束，即不可再做任何操作。
	 */
	String TRADE_FINISHED = "TRADE_FINISHED";
}
