package com.srobber.ratelimiter;

import com.srobber.common.exeption.StatusHolderException;
import com.srobber.common.status.CommonStatus;
import com.srobber.common.util.AopUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 流控AOP切面
 *
 * @author chensenlai
 * 2020-10-23 上午11:44
 */
@Slf4j
@Aspect
public class RateLimiterAspect {

    private static final String CONTEXT_OBJECT= "_object";
    private final SpelExpressionParser spelParser = new SpelExpressionParser();
    private final ConcurrentHashMap<String, Expression> spelExpressionCache = new ConcurrentHashMap<>();

    @Setter
    private RateLimiterFilter rateLimiterFilter;

    /**
     * 定义切面的关注点
     */
    @Pointcut("@annotation(com.srobber.ratelimiter.RateLimiter)")
    public void rateLimiterPointcut(){
    }

    @Around("rateLimiterPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature)) {
            throw new IllegalArgumentException("@RateLimiter must used on method");
        }

        Object object = joinPoint.getTarget();
        Method method = AopUtil.getMethod(joinPoint);
        Object[] args = joinPoint.getArgs();

        StandardEvaluationContext context = getSpelContext(object, method, args);

        RateLimiter rateLimiter = method.getAnnotation(RateLimiter.class);
        // 限流模块key, 限流阈值, 限流超时时间
        String limitKey = getSpelExpression(rateLimiter.key()).getValue(context, String.class);
        long limitTimes = rateLimiter.limit();
        long limitExpireMills = rateLimiter.expireMills();
        String message = rateLimiter.message();

        log.info("RateLimiter limitKey={} limitTimes={},limitExpireMills={}", limitKey, limitTimes, limitExpireMills);
        boolean result = rateLimiterFilter.doFilter(limitKey, limitTimes, limitExpireMills);
        if(!result) {
            log.warn("RateLimiter {} limited", limitKey);
            throw new StatusHolderException(CommonStatus.RateLimit, message);
        }
        return joinPoint.proceed();
    }

    /**
     * 初始化SpringEl上下文环境
     * @param object 对象
     * @param method 方法
     * @param args 参数
     * @return SpringEl上下文环境
     */
    private StandardEvaluationContext getSpelContext(Object object, Method method, Object[] args) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable(CONTEXT_OBJECT, object);

        ParameterNameDiscoverer pd = new DefaultParameterNameDiscoverer();
        String[] parameterNames = pd.getParameterNames(method);
        if(parameterNames != null) {
            for(int i=0,len=parameterNames.length; i<len; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }
        return context;
    }

    /**
     * 获取spel编译后表达式
     * @param key spel字符串
     * @return spel表达式
     */
    private Expression getSpelExpression(String key) {
        Expression expression = spelExpressionCache.get(key);
        if(expression == null) {
            expression = spelParser.parseExpression(key);
            spelExpressionCache.putIfAbsent(key, expression);
        }
        return expression;
    }
}
