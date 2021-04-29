package com.srobber.manager.sms;

/**
 * 短信异常
 *
 * @author chensenlai
 */
public class SmsException extends RuntimeException {

    public SmsException() {
        super("号码发送异常");
    }

    public SmsException(String message) {
        super(message);
    }
}
