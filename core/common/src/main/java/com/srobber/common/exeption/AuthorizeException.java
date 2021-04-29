package com.srobber.common.exeption;

/**
 * 用户授权异常
 *
 * @author chensenlai
 */
public class AuthorizeException extends RuntimeException {

    public AuthorizeException() {
        super("没有权限");
    }

    public AuthorizeException(String message) {
        super(message);
    }
}