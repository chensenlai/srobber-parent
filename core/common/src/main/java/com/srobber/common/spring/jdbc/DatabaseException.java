package com.srobber.common.spring.jdbc;

/**
 * 数据库操作异常
 *
 * @author chensenlai
 */
public class DatabaseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DatabaseException() {
		super();
	}

	public DatabaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public DatabaseException(String message) {
		super(message);
	}

	public DatabaseException(Throwable cause) {
		super(cause);
	}
}
