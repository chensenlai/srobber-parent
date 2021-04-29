package com.srobber.manager.im.rongcloud;

import com.srobber.manager.im.ImException;
import io.rong.models.Result;

/**
 * 融云工具类
 * @author chensenlai
 * 2020-10-26 下午4:38
 */
public class RongcloudUtil {

    /**
     * 融云成功响应码
     */
    private static final int CODE_SUCCESS = 200;

    /**
     * 检查响应成功
     * @param result
     */
    public static void checkSuccess(Result result) {
        if(result.getCode() != CODE_SUCCESS) {
            throw new ImException(result.getCode()+":"+result.getErrorMessage());
        }
    }
}
