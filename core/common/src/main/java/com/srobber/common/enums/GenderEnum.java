package com.srobber.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 性别枚举值
 *
 * @author chensenlai
 */
@Getter
@AllArgsConstructor
public enum GenderEnum implements BaseEnum {

    /**
     * 女
     */
    Female(1, "女"),
    /**
     * 男
     */
    Male(2, "男"),
    /**
     * 未知
     */
    Unknown(3, "未知")
    ;

    private int num;
    private String name;

    /**
     * 微信sex转性别枚举
     * @param sex 1-男 2-女 3-未知
     * @return 性别
     */
    public static GenderEnum ofWechatSex(int sex) {
        if(sex == 1) {
            return GenderEnum.Male;
        }
        if(sex == 2) {
            return GenderEnum.Female;
        }
        return GenderEnum.Unknown;
    }
}
