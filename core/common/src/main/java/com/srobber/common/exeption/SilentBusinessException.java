package com.srobber.common.exeption;

/**
 * 静默异常
 * 异常管理器捕获到异常后，不打印堆栈等
 *
 * @author chensenlai
 * 2020-09-17 下午5:00
 */
public class SilentBusinessException extends RuntimeException {

    public SilentBusinessException(String message) {
        super(message);
    }
}
