package com.srobber.log;

import com.srobber.log.enums.LogLevelEnum;
import com.srobber.common.util.AopUtil;
import com.srobber.common.util.StringUtil;
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
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志拦截器, 使用SpringEL解析参数,返回值等
 *
 * @author chensenlai
 */
@Slf4j
@Aspect
public class LogAspect {
	
	private static final String CONTEXT_OBJECT= "_object";
	private static final String CONTEXT_RESULT = "_result";
	private static final String CONTEXT_EXCEPTION = "_exception";

	private final SpelExpressionParser spelParser = new SpelExpressionParser();
	private final ConcurrentHashMap<String, Expression> spelExpressionCache = new ConcurrentHashMap<>();

	@Setter
	private LogHandler logHandler;

	/**
	 * 定义切面的关注点
	 */
	@Pointcut("@annotation(com.srobber.log.Log)")
	public void logPointcut(){
	}

	//TODO SpringEL表达式解析和反射缓存，比较下性能差异
	/**
	 * 定义切面增强方发
	 * @param joinPoint 关注点
	 * @return 原方法执行结果
	 * @throws Throwable 原方法抛出异常
	 */
	@Around("logPointcut()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		Signature signature = joinPoint.getSignature();
		if (!(signature instanceof MethodSignature)) {
			throw new IllegalArgumentException("@Log must used on method");
		}

		Object result = null;
		Throwable exception = null;
		try {
			result = joinPoint.proceed();
		} catch(Throwable e) {
			exception = e;
		}

		Object object = joinPoint.getTarget();
		Method method = AopUtil.getMethod(joinPoint);
		Object[] args = joinPoint.getArgs();

		Log logMeta = method.getAnnotation(Log.class);
		String condition = logMeta.condition();
		LogLevelEnum level = logMeta.level();
		String type = logMeta.type();
		String key1 = logMeta.key1();
		String key2 = logMeta.key2();
		String key3 = logMeta.key3();
		String data = logMeta.data();
		try {
			StandardEvaluationContext context = getSpelContext(object, method, args,
					result, exception);
			Boolean needLog = getSpelExpression(condition).getValue(context, Boolean.class);
			if(needLog!=null && needLog) {
				if(StringUtil.isNotBlank(key1)) {
					key1 = getSpelExpression(key1).getValue(context, String.class);
				}
				if(StringUtil.isNotBlank(key2)) {
					key2 = getSpelExpression(key2).getValue(context, String.class);
				}
				if(StringUtil.isNotBlank(key3)) {
					key3 = getSpelExpression(key3).getValue(context, String.class);
				}
				if(StringUtil.isNotBlank(data)) {
					data = getSpelExpression(data).getValue(context, String.class);
				}

				StringBuilder exceptionMsgBuf = new StringBuilder("");
				if(exception != null) {
					level = logMeta.exceptionLevel();
					exceptionMsgBuf.append("message: ").append(exception.getMessage()).append("\n");
					StackTraceElement[] stacks = exception.getStackTrace();
					if(stacks != null && stacks.length>0) {
						exceptionMsgBuf.append("stack: ").append(stacks[0].getClassName()+" "+stacks[0].getMethodName()+" "+stacks[0].getLineNumber()).append("\n");
					}
					Throwable cause = exception.getCause();
					if(cause != null) {
						stacks = cause.getStackTrace();
						if(stacks != null && stacks.length>0) {
							exceptionMsgBuf.append("cause: ").append(stacks[0].getClassName()+" "+stacks[0].getMethodName()+" "+stacks[0].getLineNumber()).append("\n");
						}
					}
				}
				String exceptionMsg = exceptionMsgBuf.toString();
				exceptionMsg = StringUtil.substring(exceptionMsg, 0, exceptionMsg.length(), 1000);

				//发布日志记录事件
				LogEvent logEvent = new LogEvent();
				logEvent.setType(type);
				logEvent.setLevel(level);
				logEvent.setKey1(key1);
				logEvent.setKey2(key2);
				logEvent.setKey3(key3);
				logEvent.setData(data);
				logEvent.setException(exceptionMsg);

				logHandler.handle(logEvent);
			}
		} catch (Throwable e) {
			log.error("log {} {} {} error.", Object.class.getName(), method.getName(), Arrays.toString(args), e);
		}

		if(exception != null) {
			throw exception;
		}
		return result;
	}

	/**
	 * 初始化SpringEl上下文环境
	 * @param object 对象
	 * @param method 方法
	 * @param args 参数
	 * @param result 方法执行结果
	 * @param exception 方法执行异常
	 * @return SpringEl上下文环境
	 */
	private StandardEvaluationContext getSpelContext(Object object, Method method, Object[] args,
														  Object result, Throwable exception) {
		StandardEvaluationContext context = new StandardEvaluationContext();
		context.setVariable(CONTEXT_OBJECT, object);
		context.setVariable(CONTEXT_RESULT, result);
		context.setVariable(CONTEXT_EXCEPTION, exception);

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
