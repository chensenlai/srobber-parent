package com.srobber.auth;

import java.lang.annotation.*;

/**
 * 方法上加该注解,表示接口需要认证用户身份
 *
 * @author chensenlai
 */
@Documented
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD) 
public @interface Authentication {

}
