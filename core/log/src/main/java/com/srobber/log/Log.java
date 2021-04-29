package com.srobber.log;

import com.srobber.log.enums.LogLevelEnum;

import java.lang.annotation.*;

/**
 * 打上@Log注解,可以自动记录日志内容
 *
 * @author chensenlai
 */
@Documented 
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD) 
public @interface Log {

	LogLevelEnum level() default LogLevelEnum.Info;
	
	LogLevelEnum exceptionLevel() default LogLevelEnum.Error;
	
	String type();
	
	String key1() default "";
	
	String key2() default "";
	
	String key3() default "";
	
	String data();
	
	String condition() default "true";
}
