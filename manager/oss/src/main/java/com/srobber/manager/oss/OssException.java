package com.srobber.manager.oss;

/**
 * OSS上传异常
 *
 * @author chensenlai
 */
public class OssException extends RuntimeException {

    public OssException() {
        super("OSS失败");
    }

    public OssException(String message) {
        super(message);
    }
}
