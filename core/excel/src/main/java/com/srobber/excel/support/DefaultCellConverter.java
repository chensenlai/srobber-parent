package com.srobber.excel.support;

import com.srobber.excel.CellConverter;

import java.util.Map;

/**
 * cell值不做转化
 *
 * @author chensenlai
 */
public class DefaultCellConverter implements CellConverter<Object> {

	@Override
	public Object cellToField(String cellValue, Map<String, Object> contextData) {
		return cellValue;
	}

	@Override
	public String fieldToCell(Object fieldValue, Map<String, Object> contextData) {
		return fieldValue == null ? null : fieldValue.toString();
	}
}
