package com.srobber.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审核状态
 *
 * @author chensenlai
 * 2020-09-23 下午2:44
 */
@Getter
@AllArgsConstructor
public enum AuditStateEnum implements BaseEnum {

    NoNeed(1, "不用审"),
    ToAudit(2, "待审核"),
    Pass(3, "已通过"),
    NoPass(4, "不通过"),
    ;
    private int num;
    private String name;
}