package com.srobber.common.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 生日校验器
 *
 * @author chensenlai
 * 2020-09-23 下午2:52
 */
@Documented
@Constraint(validatedBy = {BirthDateValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BirthDate {

    String message() default "生日格式不正确(yyyy-MM-dd)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
