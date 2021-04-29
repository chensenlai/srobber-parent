package com.srobber.common.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 身份证校验器
 * {@link IdCardValidator}
 *
 * @author chensenlai
 */
@Documented
@Constraint(validatedBy = {IdCardValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IdCard {

    String message() default "身份证合法性校验不通过";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
