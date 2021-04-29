package com.srobber.common.util;

import com.srobber.common.exeption.WrapException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * AOP工具类
 *
 * @author chensenlai
 */
@Slf4j
public class AopUtil {

    /**
     * 根据ProceedingJoinPoint获取对应拦截Method
     * @param joinPoint 拦截点
     * @return 拦截点对应方法
     */
    public static Method getMethod(ProceedingJoinPoint joinPoint) {
        Object target = joinPoint.getTarget();
        Signature signature = joinPoint.getSignature();
        if(!(signature instanceof MethodSignature)) {
            throw new UnsupportedOperationException("joinPoint signature "+signature.getDeclaringType()+" not support");
        }
        MethodSignature methodSignature = (MethodSignature)signature;
        try {
            return target.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        } catch (Exception e) {
            log.error("joinPoint getMethod error. {}", e.getMessage());
            throw new WrapException(e);
        }
    }

    /**
     * 通过代理后的对象找到目标对象
     * @param proxy 代理后的对象
     * @return 目标对象
     * @throws Exception 异常
     */
    public Object getTarget(Object proxy) throws Exception {
        if(!AopUtils.isAopProxy(proxy)) {
            //不是代理对象
            return proxy;
        }

        if(AopUtils.isJdkDynamicProxy(proxy)) {
            //JDK
            return getJdkDynamicProxyTargetObject(proxy);
        } else {
            //cglib
            return getCglibProxyTargetObject(proxy);
        }
    }

    /**
     * CGLIB方式被代理类的获取
     * @param proxy 代理后的对象
     * @return 目标对象
     * @throws Exception 异常
     */
    private Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        Object target = ((AdvisedSupport)advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
        return target;
    }

    /**
     * JDK被代理类的获取
     * @param proxy 代理后的对象
     * @return 目标对象
     * @throws Exception 异常
     */
    private Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy);
        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        Object target = ((AdvisedSupport)advised.get(aopProxy)).getTargetSource().getTarget();
        return target;
    }


    private AopUtil(){}

}
