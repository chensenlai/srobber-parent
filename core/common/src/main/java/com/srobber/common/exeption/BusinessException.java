package com.srobber.common.exeption;

/**
 * 业务异常
 *
 * @author chensenlai
 */
public class BusinessException extends RuntimeException {

	public BusinessException() {
		super("业务处理异常");
	}

	public BusinessException(String message) {
		super(message);
	}

}
