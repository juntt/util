package common.util.stereotype.enhancer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import common.util.specific.RequestSpecific;
import common.util.stereotype.handler.StereotypeHandler;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 拦截器处理自定义注解
 * 
 * @author jieli
 *
 */
public class JMethodInterceptor implements MethodInterceptor {
	/**
	 * 拦截处理
	 * 
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
		// 方法名
		String methodName = obj.getClass().getSimpleName() + "." + method.getName();
		// 方法的注解数组
		Annotation[] annotations = method.getAnnotations();
		// 业务ID
		Object pid = RequestSpecific.parsePid(args);

		try {
			StereotypeHandler.before(annotations, pid, methodName, args);
			Object response = proxy.invokeSuper(obj, args);
			StereotypeHandler.after(annotations, pid, methodName, response);
			return response;
		} catch (Throwable e) {
			StereotypeHandler.throwing(annotations, pid, methodName, e);
			throw e;
		}
	}
}
