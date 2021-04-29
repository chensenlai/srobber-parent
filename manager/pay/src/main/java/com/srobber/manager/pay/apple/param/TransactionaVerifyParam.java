package com.srobber.manager.pay.apple.param;


import lombok.Data;

/**
 * 事务校验参数
 *
 * @author chensenlai
 */
@Data
public class TransactionaVerifyParam {

	/**
	 * 苹果支付交易流水号
	 */
	private String transactionalId;
	/**
	 * 支付关联订单号 
	 */
	private String orderNo;
}
