package com.srobber.manager.realname;

import com.srobber.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author chensenlai
 * 2020-10-15 上午10:55
 */
@Getter
@AllArgsConstructor
public enum RealnameResultEnum implements BaseEnum {

    Pass(1, "认证通过"),
    NoPass(2, "认证不通过"),
    ;

    private int num;
    private String name;
}