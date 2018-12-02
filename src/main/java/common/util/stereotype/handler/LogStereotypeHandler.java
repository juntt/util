package common.util.stereotype.handler;

import common.util.log.ILog;
import common.util.log.Logger;
import common.util.tools.JsonUtils;

/**
 * 日志注解处理器
 * 
 * @author jieli
 *
 */
public class LogStereotypeHandler implements IStereotypeHandler {
	private ILog log = new Logger();

	@Override
	public Object before(Object pid, String methodName, Object... args) {
		log.infoPid(pid, methodName, "request: " + JsonUtils.convert2Json(args));
		return null;
	}

	@Override
	public Object after(Object pid, String methodName, Object... args) {
		log.infoPid(pid, methodName, "response: " + JsonUtils.convert2Json(args));
		return null;
	}

	@Override
	public Object throwing(Object pid, String methodName, Throwable e) {
		log.errorPid(pid, methodName, e);
		return null;
	}
}
