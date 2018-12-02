package common.util.stereotype.handler;

/**
 * 自定义注解处理器接口
 * 
 * @author jieli
 */
public interface IStereotypeHandler {
	Object before(Object pid, String methodName, Object... args);

	Object after(Object pid, String methodName, Object... args);

	Object throwing(Object pid, String methodName, Throwable e);
}
