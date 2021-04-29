package com.srobber.datasource;

import java.lang.annotation.*;

/**
 * 指定具体数据源
 *
 * @author chensenlai
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {


    Group group() default Group.Default;

    String key() default "";

    /**
     * 主从分组
     */
    enum Group {
        /**
         * 默认
         */
        Default,
        /**
         * 主库
         */
        Master,
        /**
         * 从库
         */
        Slave
    }
}