package com.srobber.common.jump;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 跳转参数定义
 *
 * @author chensenlai
 * 2020-12-15 下午5:00
 */
@Data
@AllArgsConstructor
public class ParamDefinition {

    /**
     * 参数名
     */
    private String name;
    /**
     * 参数占位符
     */
    private String placeholder;
    /**
     * 是否必填
     */
    private boolean required;
}
