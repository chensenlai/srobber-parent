package com.srobber.auth;

import java.lang.annotation.*;

/**
 * 用于springMVC参数解析,标记用户的登录信息
 *
 * @author chensenlai
 */
@Documented
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.PARAMETER) 
public @interface UserLogin {
}
