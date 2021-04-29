package com.srobber.common.exeption;

/**
 * 用户身份认证异常
 *
 * @author chensenlai
 **/
public class AuthenticateException extends RuntimeException {

    public AuthenticateException() {
        super("认证异常");
    }

    public AuthenticateException(String message) {
        super(message);
    }
}