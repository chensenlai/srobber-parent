package com.srobber.common.status;


/**
 *
 * 状态码和状态信息。要求做到模块状态码区分 <br>
 * 业务的状态码以万做为起始。<br>
 * 1~9999(含) 已占用，用于通用的状态描述，比如db异常
 *
 * @author chensenlai
 */
public interface BaseStatus {

	/**
	 * 状态码
	 * @return 状态码
	 */
	int getCode();

	/**
	 * 状态描述
	 * @return 状态描述
	 */
	String getMsg();

}
