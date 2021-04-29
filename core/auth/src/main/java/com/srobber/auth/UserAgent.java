package com.srobber.auth;

import java.lang.annotation.*;

/**
 * 用于springMVC参数解析,标记用户的客户端信息
 *
 * @author chensenlai
 */
@Documented
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.PARAMETER) 
public @interface UserAgent {
}
