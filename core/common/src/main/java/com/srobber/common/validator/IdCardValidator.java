package com.srobber.common.validator;

import com.srobber.common.util.CheckUtil;
import com.srobber.common.util.StringUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 身份证校验器具体实现
 *
 * @author chensenlai
 */
public class IdCardValidator implements ConstraintValidator<IdCard,String> {


    @Override
    public void initialize(IdCard constraintAnnotation) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(StringUtil.isBlank(value)){
            return true;
        }
        return CheckUtil.checkIdCard(value);
    }
}