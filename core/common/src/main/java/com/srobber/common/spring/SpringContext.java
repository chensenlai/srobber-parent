package com.srobber.common.spring;

import com.srobber.common.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.*;
import org.springframework.stereotype.Component;

/**
 * Spring应用上下文
 * 1 通过名称或类型从上下文对象获取Bean实例
 * 2 发布Spring事件
 *
 * @author chensenlai
 */
@Slf4j
@Component
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext ctx;

    /**
     * 获取BeanFactory管理的Bean对象
     * @param name Bean名称
     * @return bean实例
     */
    public static <T> T getBean(String name) {
        return (T) ctx.getBean(name);
    }

    /**
     * 获取BeanFactory管理的requiredType类型的对象
     * @param requiredType Bean的类型
     * @return Bean的实例
     */
    public static <T> T getBean(Class<T> requiredType) {
        return (T) ctx.getBean(requiredType);
    }

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     * @param name Bean名称
     * @return boolean 是否存在该Bean
     */
    public static boolean containsBean(String name) {
        return ctx.containsBean(name);
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。
     * 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     * @param name Bean名称
     * @return boolean 是否单例
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return ctx.isSingleton(name);
    }

    /**
     * 获取给定名称的bean类型
     * @param name bean名称
     * @return Class 注册对象的类型
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return ctx.getType(name);
    }

    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     * @param name bean名称
     * @return bean对应的别名
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return ctx.getAliases(name);
    }

    /**
     * Spring 发布事件, 用于应用内解耦合
     * 监听器会同步处理事件, 区别于消息中间件, 是应用内部事件流转处理
     * @param event
     */
    public static void publicEvent(BaseEvent event) {
        ctx.publishEvent(event);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }
}
