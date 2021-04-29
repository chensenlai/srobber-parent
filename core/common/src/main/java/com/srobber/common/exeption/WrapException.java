package com.srobber.common.exeption;

/**
 * 包装异常
 * 用于把检查异常报装成RuntimeException, 往上抛
 *
 * @author chensenlai
 */
public class WrapException extends RuntimeException {

    public WrapException(Throwable cause) {
        super(cause);
    }
}
