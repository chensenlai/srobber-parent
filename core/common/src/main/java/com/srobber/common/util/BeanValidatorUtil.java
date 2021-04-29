package com.srobber.common.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;

/**
 * Bean有合法校验工具类
 * 使用hibernate validator
 *
 * @author chensenlai
 */
public class BeanValidatorUtil {

	/**
	 * 校验bean合法性
	 * @param target 目标对象
	 * @param groups 分组名称
	 * @return 错误列表 (如果校验成功,返回长度为0列表)
	 */
	public static <T> List<String> validateBean(T target, Class<?>... groups) {
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(target, groups);
        if(constraintViolations == null || constraintViolations.isEmpty()) {
        	return Collections.emptyList();
        }
        List<String> messages = new ArrayList<>(constraintViolations.size());
    	for(ConstraintViolation<T> constraintViolation : constraintViolations) {
    		messages.add(constraintViolation.getMessage());
    	}
    	return messages;
	 }

	/**
	 * 校验bean方法参数合法性
	 * @param target 目标对象
	 * @param method 目标方法
	 * @param args 目标参数
	 * @param groups 分组名称
	 * @return 错误列表 (如果校验成功,返回长度为0列表)
	 */
	public static <T> List<String> validateMethodParameters(T target, Method method, Object[] args, Class<?>... groups) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	    ExecutableValidator executableValidator = factory.getValidator().forExecutables();
        Set<ConstraintViolation<T>> constraintViolations = executableValidator.validateParameters(target, method, args, groups);
        if(constraintViolations==null || constraintViolations.isEmpty()) {
        	return Collections.emptyList();
        }
        List<String> messages = new ArrayList<>(constraintViolations.size());
    	for(ConstraintViolation<T> constraintViolation : constraintViolations) {
    		messages.add(constraintViolation.getMessage());
    	}
    	return messages;
	 }

	 private BeanValidatorUtil(){}
}
