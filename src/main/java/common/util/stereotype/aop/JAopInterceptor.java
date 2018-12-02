package common.util.stereotype.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import common.util.specific.RequestSpecific;
import common.util.stereotype.handler.StereotypeHandler;

/**
 * AOP处理自定义注解
 * 
 * @author jieli
 *
 */
@Aspect
@Component
public class JAopInterceptor {
	@Pointcut("@annotation(common.util.stereotype.JLog)")
	public void pc() {
	}

	@Around("pc()")
	public Object around(ProceedingJoinPoint jp) throws Throwable {
		MethodSignature methodSignature = (MethodSignature) jp.getSignature();
		Method targetMethod = methodSignature.getMethod();
		Method realMethod = jp.getTarget().getClass().getDeclaredMethod(methodSignature.getName(),
				targetMethod.getParameterTypes());
		// 方法名
		String methodName = jp.getTarget().getClass().getSimpleName() + "." + realMethod.getName();
		// 方法的注解数组
		Annotation[] annotations = realMethod.getAnnotations();
		// 方法的参数数组
		Object[] args = jp.getArgs();
		// 业务ID
		Object pid = RequestSpecific.parsePid(args);

		try {
			StereotypeHandler.before(annotations, pid, methodName, args);
			// 控制权交还给方法
			Object response = jp.proceed();
			StereotypeHandler.after(annotations, pid, methodName, response);
			return response;
		} catch (Throwable e) {
			StereotypeHandler.throwing(annotations, pid, methodName, e);
			throw e;
		}
	}
}
