package com.srobber.datasource;

import com.srobber.common.util.StringUtil;
import com.srobber.datasource.config.MasterSlaveDataSourceProperties;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 选择数据源的增强
 * 这里没有使用Aspect注解实现是想灵活定制Pointcut.
 * 由业务方指定Pointcut表达式(使用AspectJ的表达式)
 *
 * 注意: 切面优先级必须高于@Transactional, 事务开启getConnection会触发数据源路由选择
 *
 * @author chensenlai
 */
@Slf4j
public class TargetDataSourceAdvisor extends DefaultPointcutAdvisor implements BeanFactoryAware {

    private BeanFactory beanFactory;

    @Setter
    private MasterSlaveDataSourceProperties dataSourceProperties;

    @PostConstruct
    public void init() {
        this.setAdvice(buildAdvice());
        this.setPointcut(buildPointcut());
    }

    /**
     * 构建主从数据库方法增强
     * 1 由注解@TargetDataSource指定数据源
     * 2 由注解@Transactional事务只读属性判断
     * 3 由方法名推断方法是否只读
     * @return 增强
     */
    public Advice buildAdvice() {
        return (MethodInterceptor) invocation -> {
            String oldKey = MasterSlaveDataSourceContext.get();
            String key = oldKey;
            if(key == null) {
                key = deduceFromTargetDataSource(invocation);
            }
            if(key == null) {
                key = deduceFromTransactional(invocation);
            }
            if(key == null) {
                key = deduceFromMethodName(invocation);
            }

            try {
                if(key == null) {
                    key = MasterSlaveDataSourceContext.getMaster();
                }
                MasterSlaveDataSourceContext.set(key);
                return invocation.proceed();
            } catch(Throwable e) {
                throw e;
            } finally {
                MasterSlaveDataSourceContext.set(oldKey);
            }
        };
    }

    /**
     * 构建主从数据库方法切面
     * 1 用户指定拦截Pointcut, 以用户配置为准
     * 2 否则默认对加上@TargetDataSource或@Transactional类或方法切面启用增强
     * @return 切面
     */
    public Pointcut buildPointcut() {
        //用户指定拦截Pointcut, 以用户配置为准
        String pointcutExpression = dataSourceProperties.getPointcutExpression();
        if(StringUtil.isNotBlank(pointcutExpression)) {
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression(pointcutExpression);
            pointcut.setBeanFactory(this.beanFactory);
            return pointcut;
        }
        //否则默认对加上@TargetDataSource或@Transactional类或方法切面启用增强
        //指定数据源切面: @TargetDataSource类和方法
        String targetDataSourceExpression = "@annotation(com.srobber.datasource.TargetDataSource) || @within(com.srobber.datasource.TargetDataSource)";
        AspectJExpressionPointcut targetDataSourcePointcut = new AspectJExpressionPointcut();
        targetDataSourcePointcut.setExpression(targetDataSourceExpression);
        targetDataSourcePointcut.setBeanFactory(this.beanFactory);
        //事务切面: @Transactional类和方法
        String transactionalExpression = "@annotation(org.springframework.transaction.annotation.Transactional) || @within(org.springframework.transaction.annotation.Transactional)";
        AspectJExpressionPointcut transactionalPointcut = new AspectJExpressionPointcut();
        transactionalPointcut.setExpression(transactionalExpression);
        transactionalPointcut.setBeanFactory(this.beanFactory);
        return new ComposablePointcut((Pointcut)targetDataSourcePointcut).union(new ComposablePointcut((Pointcut)transactionalPointcut));
    }

    /**
     * 根据指定数据源推断走主库还是从库
     * @param invocation 执行方法
     * @return 数据库key
     */
    private String deduceFromTargetDataSource(MethodInvocation invocation) {
        String key = null;
        Method method = invocation.getMethod();
        TargetDataSource targetDataSource = AnnotationUtils.getAnnotation(method, TargetDataSource.class);
        if(targetDataSource == null) {
            targetDataSource = AnnotationUtils.getAnnotation(invocation.getThis().getClass(), TargetDataSource.class);
        }
        if(targetDataSource != null) {
            if(StringUtil.isNotBlank(targetDataSource.key())) {
                key = targetDataSource.key();
            } else if(targetDataSource.group() == TargetDataSource.Group.Master) {
                key = MasterSlaveDataSourceContext.getMaster();
            } else if(targetDataSource.group() == TargetDataSource.Group.Slave) {
                key = MasterSlaveDataSourceContext.getSlave();
            }
        }
        return key;
    }

    /**
     * 根据事务只读属性配置推断走主库还是从库
     * @param invocation 执行方法
     * @return 数据库key
     */
    private String deduceFromTransactional(MethodInvocation invocation) {
        String key = null;
        Method method = invocation.getMethod();
        Transactional transactional = AnnotationUtils.getAnnotation(method, Transactional.class);
        if(transactional == null) {
            transactional = AnnotationUtils.getAnnotation(invocation.getThis().getClass(), Transactional.class);
        }
        if(transactional != null) {
            if(transactional.readOnly()) {
                key = MasterSlaveDataSourceContext.getSlave();
            } else {
                key = MasterSlaveDataSourceContext.getMaster();
            }
        }
        return key;
    }

    /**
     * 根据方法名字推断数据源走主库还是从库
     * @param invocation 执行方法
     * @return 数据库key
     */
    private String deduceFromMethodName(MethodInvocation invocation) {
        String key = null;
        List<String> readonlyMethodList = dataSourceProperties.getReadonlyMethodList();
        if(readonlyMethodList == null || readonlyMethodList.isEmpty()) {
            readonlyMethodList = MasterSlaveDataSourceProperties.READONLY_METHOD_LIST;
        }
        String methodName = invocation.getMethod().getName();
        for(String readOnlyMethod : readonlyMethodList) {
            if(methodName.startsWith(readOnlyMethod)) {
                key = MasterSlaveDataSourceContext.getSlave();
                break;
            }
        }
        return key;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
