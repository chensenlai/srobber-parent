package com.srobber.common.validator;

import com.srobber.common.util.StringUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 密码强度校验器具体实现
 *
 * @author chensenlai
 */
public class PasswordValidator implements ConstraintValidator<Password,String> {

    private Password.Strength strength;

    @Override
    public void initialize(Password constraintAnnotation) {
        this.strength = constraintAnnotation.strength();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(StringUtil.isBlank(value)){
            return true;
        }
        return strength.getValue().matcher(value).find();
    }
}