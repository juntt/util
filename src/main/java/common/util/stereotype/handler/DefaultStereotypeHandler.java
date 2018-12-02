	package common.util.stereotype.handler;

/**
 * 默认的注解处理器
 * 
 * @author jieli
 *
 */
public class DefaultStereotypeHandler implements IStereotypeHandler {
	@Override
	public Object before(Object pid, String methodName, Object... args) {
		return null;
	}

	@Override
	public Object after(Object pid, String methodName, Object... args) {
		return null;
	}

	@Override
	public Object throwing(Object pid, String methodName, Throwable e) {
		return null;
	}
}
