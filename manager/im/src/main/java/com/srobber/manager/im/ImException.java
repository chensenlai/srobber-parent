package com.srobber.manager.im;

/**
 * IM异常
 *
 * @author chensenlai
 * 2020-10-26 下午4:40
 */
public class ImException extends RuntimeException {

    public ImException(String message) {
        super(message);
    }

    public ImException(Throwable cause) {
        super(cause);
    }
}
