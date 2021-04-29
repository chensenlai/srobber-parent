package com.srobber.common.jump;

import com.srobber.common.exeption.ParamException;

import java.util.Map;

/**
 * 跳转地址构建器
 * @author chensenlai
 * 2020-12-15 下午4:59
 */
public interface BaseParamBuilder {

    /**
     * 构建跳转地址
     * @param jumpParams 跳转参数
     * @return 跳转地址
     * @throws ParamException
     */
    String build(Map<String, Object> jumpParams) throws ParamException;
}
