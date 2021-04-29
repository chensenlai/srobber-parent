package com.srobber.excel;

import com.srobber.excel.support.DefaultCellConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * excel与bean字段映射关系注解
 * 标记excel文件处理相关属性
 *
 * @author chensenlai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD })
public @interface ExcelField {

	/**
	 * 字段名
	 */
	String name() default "";

	/**
	 * excel单元转化成javabean字段对应值
	 */
	Class<? extends CellConverter> converter() default DefaultCellConverter.class;
}
