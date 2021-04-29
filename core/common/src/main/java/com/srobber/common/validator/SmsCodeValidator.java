package com.srobber.common.validator;

import com.srobber.common.util.StringUtil;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 验证码校验器具体实现
 *
 * @author chensenlai
 */
public class SmsCodeValidator implements ConstraintValidator<SmsCode, String> {

	private static final String SMS_CODE_PATTERN = "^[0-9]{4}$"; 
	
	@Override
	public void initialize(SmsCode constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if(StringUtil.isBlank(value)) {
			return true;
		}
		return Pattern.matches(SMS_CODE_PATTERN, value);
	}

}
