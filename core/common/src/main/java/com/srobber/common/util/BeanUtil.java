package com.srobber.common.util;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;

/**
 * JavaBean工具类
 *
 * @author chensenlai
 */
public class BeanUtil {

	/**
	 * 获取属性名对应getter方法
	 * @param property 属性名
	 * @param javaType 属性类型
	 * @return
	 */
	public static String getGetterMethodName(String property, Class<?> javaType) {
        StringBuilder getterBuf = new StringBuilder(property.length()+3);
		//Boolean是get方法
        if ("boolean".equals(javaType.getName())) {
        	getterBuf.append("is"); 
        } else {
        	getterBuf.append("get"); 
        }
        char ch = property.charAt(0);
        if (Character.isLowerCase(ch)) {
            if (property.length() == 1 || !Character.isUpperCase(property.charAt(1))) {
            	ch = Character.toUpperCase(ch);
            }
        }
        getterBuf.append(ch).append(property.substring(1, property.length()));
        return getterBuf.toString();
    }

	/**
	 * 获取属性名对应setter方法
	 * @param property 属性名
	 * @return
	 */
	public static String getSetterMethodName(String property) {
        StringBuilder setterBuf = new StringBuilder(property.length()+3);
        setterBuf.insert(0, "set"); 
        char ch = property.charAt(0);
        if (Character.isLowerCase(ch)) {
            if (property.length() == 1 || !Character.isUpperCase(property.charAt(1))) {
            	ch = Character.toUpperCase(ch);
            }
        }
        setterBuf.append(ch).append(property.substring(1, property.length()));
        return setterBuf.toString();
	}
	
	/**
	 * bean拷贝工具类
	 * 可以拷贝的属性条件,属性同名且可继承
	 * @param src 源实体
	 * @param target 目标实体
	 * @return
	 */
	public static void copyBean(Object src, Object target) {
		if(src == null || target == null) {
			return;
		}
		BeanCopier copier = BeanCopier.create(src.getClass(), target.getClass(), false);
		copier.copy(src, target, null);
	}

	/**
	 * bean拷贝工具类
	 * 可以拷贝的属性条件,属性同名且可继承
	 * @param src 源实体
	 * @param target 目标实体
	 * @param converter 类型转化器
	 */
	public static void copyBean(Object src, Object target, Converter converter) {
		if(src == null || target == null) {
			return;
		}
		if(converter == null) {
			copyBean(src, target);
			return;
		}
		BeanCopier copier = BeanCopier.create(src.getClass(), target.getClass(), true);
		copier.copy(src, target, converter);
		return;
	}

	private BeanUtil(){}
}
