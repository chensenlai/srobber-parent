package com.srobber.common.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 用户名校验器
 * {@link UserNameValidator}
 *
 * @author chensenlai
 */
@Documented
@Constraint(validatedBy = {UserNameValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserName {

    String message() default "用户名格式错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
