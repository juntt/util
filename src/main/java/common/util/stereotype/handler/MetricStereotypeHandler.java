package common.util.stereotype.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import common.util.log.ILog;
import common.util.log.Logger;
import common.util.tools.StopWatchUtils;

/**
 * 接口度量(调用次数、耗时)处理器
 * 
 * @author jieli
 *
 */
public class MetricStereotypeHandler implements IStereotypeHandler {
	private ILog log = new Logger();
	private static Map<String, AtomicLong> count = new ConcurrentHashMap<>();

	@Override
	public Object before(Object pid, String methodName, Object... args) {
		StopWatchUtils.start();
		// 调用次数+1
		if (count.putIfAbsent(methodName, new AtomicLong(1)) == null)
			count.put(methodName, new AtomicLong(1)).incrementAndGet();
		else
			count.get(methodName).incrementAndGet();
		return null;
	}

	@Override
	public Object after(Object pid, String methodName, Object... args) {
		// 执行耗时
		log.infoPid(pid, methodName, "elapse (ms): " + StopWatchUtils.stop() + " called:" + count.get(methodName).get());
		return null;
	}

	@Override
	public Object throwing(Object pid, String methodName, Throwable e) {
		// 执行耗时
		log.infoPid(pid, methodName, "elapse (ms): " + StopWatchUtils.stop() + " called:" + count.get(methodName).get());
		return null;
	}
}
