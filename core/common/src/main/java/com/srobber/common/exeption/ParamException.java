package com.srobber.common.exeption;

/**
 * 参数异常
 *
 * @author chensenlai
 **/
public class ParamException extends RuntimeException {

    public ParamException() {
        super("参数异常");
    }

    public ParamException(String message) {
        super(message);
    }
}