package com.srobber.excel.support;

import com.srobber.excel.BeanValidator;

import java.util.Map;

/**
 * 不做任何校验
 *
 * @author chensenlai
 */
public class DefaultBeanValidator implements BeanValidator<Object> {

	@Override
	public String validate(Object object, Map<String, Object> contextData) {
		return "";
	}

}
