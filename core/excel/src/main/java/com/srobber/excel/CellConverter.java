package com.srobber.excel;

import java.util.Map;

/**
 * excel的cell值和javabean字段值互相转化
 *
 * @author chensenlai
 */
public interface CellConverter<T> {

	/**
	 * cell值转化成javabean字段值
	 * @param cellValue excel对应cell的值
	 * @param contextData 上下文数据,excel转化时传入
	 * @return javabean对应字段值
	 */
	T cellToField(String cellValue, Map<String, Object> contextData);

	/**
	 * javabean字段值转化成cell值
	 * @param field javabean字段值
	 * @param contextData 上下文数据,excel转化传入
	 * @return cell对应值
	 */
	String fieldToCell(T field, Map<String, Object> contextData);
}
