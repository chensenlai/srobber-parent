package com.srobber.common.validator;

import com.srobber.common.util.CheckUtil;
import com.srobber.common.util.StringUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 手机号码校验器具体实现
 *
 * @author chensenlai
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {

	@Override
	public void initialize(Phone constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if(StringUtil.isBlank(value)) {
			return true;
		}
		return CheckUtil.checkPhone(value);
	}

}
