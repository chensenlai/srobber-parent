package com.srobber.common.enums;

/**
 * 枚举值基类
 * java提供枚举值ordinal数值和位置有关,容易出错.
 * 提供基类约束系统定义枚举值必须带有固定数值和名称.
 * java转存数据库会转化成数值存储, 不使用数据库枚举类型存储, 避免数据库DDL操作.
 * java返回客户端的枚举值用数值表示.
 *
 * @author chensenlai
 */
public interface BaseEnum {

    /**
     * 枚举数值表示
     * @return 数值
     */
    int getNum();

    /**
     * 枚举名称表示
     * @return 名称
     */
    String getName();
}
