package common.util.stereotype.enhancer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import common.util.specific.RequestSpecific;
import common.util.stereotype.JRetry;
import common.util.stereotype.handler.StereotypeHandler;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 拦截器处理自定义Retry注解
 * 
 * @author jieli
 *
 */
@Deprecated
public class RetryMethodInterceptor implements MethodInterceptor {
	// 已重试的次数
	private long retriedCount = 0;

	/**
	 * @param obj
	 *            this对象
	 * @param method
	 *            方法
	 * @param args
	 *            方法的参数数组
	 * @param proxy
	 */
	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		JRetry jRetry = method.getAnnotation(JRetry.class);
		if (jRetry == null) {
			return invoke(obj, method, args, proxy, true);
		} else {
			try {
				return invoke(obj, method, args, proxy, true);
			} catch (Exception e) {
				if (retriedCount < jRetry.maxAttempts()) {
					retriedCount++;
					return invoke(obj, method, args, proxy, false);
				} else {
					StereotypeHandler.throwing(method.getAnnotations(), RequestSpecific.parsePid(args),
							method.getName(), e);
					throw e;
				}
			}
		}
	}

	private Object invoke(Object obj, Method method, Object[] args, MethodProxy proxy, boolean isSuper)
			throws Throwable {
		// 方法名
		String methodName = method.getName();
		// 方法的注解数组
		Annotation[] annotations = method.getAnnotations();
		// 业务ID
		Object pid = RequestSpecific.parsePid(args);

		StereotypeHandler.before(annotations, pid, methodName, args);
		Object response = isSuper ? proxy.invokeSuper(obj, args) : proxy.invoke(obj, args);
		StereotypeHandler.after(annotations, pid, methodName, response);
		return response;
	}
}
