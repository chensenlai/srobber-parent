package com.srobber.manager.phone;

/**
 * 号码服务异常
 *
 * @author chensenlai
 */
public class PhoneException extends RuntimeException {

    public PhoneException() {
        super("号码失败");
    }

    public PhoneException(String message) {
        super(message);
    }
}
