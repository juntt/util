package common.util.stereotype.handler;

import java.lang.annotation.Annotation;

import common.util.stereotype.JLog;
import common.util.stereotype.JMetric;

/**
 * 自定义注解处理器
 * 
 * @author jieli
 *
 */
public class StereotypeHandler {
	/**
	 * 
	 * @param annotation
	 * @return 注解对应的处理器
	 */
	private static IStereotypeHandler choice(Annotation annotation) {
		if (annotation instanceof JLog)
			return new LogStereotypeHandler();
		if (annotation instanceof JMetric)
			return new MetricStereotypeHandler();
		return new DefaultStereotypeHandler();
	}

	public static void before(Annotation[] annotations, Object pid, String methodName, Object... args) {
		for (Annotation annotation : annotations) {
			StereotypeHandler.choice(annotation).before(pid, methodName, args);
		}
	}

	public static void after(Annotation[] annotations, Object pid, String methodName, Object... args) {
		for (Annotation annotation : annotations) {
			StereotypeHandler.choice(annotation).after(pid, methodName, args);
		}
	}

	public static void throwing(Annotation[] annotations, Object pid, String methodName, Throwable e) {
		for (Annotation annotation : annotations) {
			StereotypeHandler.choice(annotation).throwing(pid, methodName, e);
		}
	}
}
