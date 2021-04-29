package com.srobber.common.exeption;

/**
 * 用户手机号码未绑定
 *
 * @author chensenlai
 **/
public class PhoneUnbindException extends RuntimeException{

    public PhoneUnbindException() {
        super("手机号码未绑定");
    }

    public PhoneUnbindException(String message) {
        super(message);
    }
}