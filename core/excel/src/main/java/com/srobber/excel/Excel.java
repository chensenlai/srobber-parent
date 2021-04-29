package com.srobber.excel;

import com.srobber.excel.support.DefaultBeanValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * excel映射关系注解
 * 标记excel文件处理相关属性
 *
 * @author chensenlai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
public @interface Excel {

	/**
	 * excel的第${sheet}个sheet
	 * 第一个sheet下标是0
	 */
	int sheet() default 0;

	/**
	 * excel的标题是第${titleRow}行
	 * 第一行是下标0
	 */
	int titleRow() default 0;

	/**
	 * excel的数据是第${dataRow}行开始
	 * 第二行是下标1
	 */
	int dataRow() default 1;

	/**
	 * excel单元转化成javabean校验器,校验javabean数据合法性
	 */
	Class<? extends BeanValidator> validator() default DefaultBeanValidator.class;

	/**
	 * excel的数据行处理结果是第${resultCol}列
	 * return -1表示不记录结果列, 第一列是0
	 */
	int resultCol() default -1;

	/**
	 * excel的数据行处理结果提示信息是第${msgCol}列
	 * return -1表示不记录结果列提示信息, 第一列是0
	 */
	int msgCol() default -1;
}
