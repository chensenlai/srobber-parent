package com.srobber.common.status;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 1~9999(含) 已占用，用于通用的状态描述，比如db异常
 *
 * @author chensenlai
 */
@Getter
@AllArgsConstructor
public enum CommonStatus implements BaseStatus {

	/**
	 * 成功
	 */
	Success(200, "成功"),
	/**
	 * 参数异常
	 */
	ParamInvalid(400, "参数异常"),

	/**
	 * 没有登陆
	 */
	NoAuthenticate(401, "没有登陆"),

	/**
	 * 号码没绑定
	 */
	PhoneUnbind(402, "号码没绑定"),

	/**
	 * 没有权限
	 */
	NoAuthorize(403, "没有权限"),

	/**
	 * 签名异常
	 */
	SignFail(409, "签名异常"),

	/**
	 * 流控限制
	 */
	RateLimit(410, "请求过于频繁"),

	/**
	 * 未处理异常
	 */
	Fail(500, "未处理的异常")
	;
	
	private final int code;
	private final String msg;
}
