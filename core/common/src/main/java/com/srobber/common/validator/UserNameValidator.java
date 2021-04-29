package com.srobber.common.validator;

import com.srobber.common.util.CheckUtil;
import com.srobber.common.util.StringUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 用户名校验具体实现
 *
 * @author chensenlai
 */
public class UserNameValidator implements ConstraintValidator<UserName, String> {

    @Override
    public void initialize(UserName constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(StringUtil.isBlank(value)) {
            return true;
        }
        return CheckUtil.checkName(value);
    }
}