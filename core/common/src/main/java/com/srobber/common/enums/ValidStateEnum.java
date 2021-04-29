package com.srobber.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 有效状态
 *
 * @author chensenlai
 * 2020-10-20 下午5:40
 */
@Getter
@AllArgsConstructor
public enum ValidStateEnum implements BaseEnum {

    Valid(1, "有效"),
    InValid(2, "失效"),
    ;

    private int num;
    private String name;
}