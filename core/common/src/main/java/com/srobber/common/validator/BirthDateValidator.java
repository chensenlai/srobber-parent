package com.srobber.common.validator;

import com.srobber.common.util.CheckUtil;
import com.srobber.common.util.StringUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 出生日期校验器
 *
 * @author chensenlai
 * 2020-09-23 下午2:53
 */
public class BirthDateValidator implements ConstraintValidator<BirthDate,String> {


    @Override
    public void initialize(BirthDate constraintAnnotation) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(StringUtil.isBlank(value)){
            return true;
        }
        return CheckUtil.checkBirthDate(value);
    }
}
