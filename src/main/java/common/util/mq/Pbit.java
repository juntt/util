package common.util.mq;

import java.util.concurrent.atomic.AtomicLong;

import common.util.log.ILog;
import common.util.log.Logger;

/**
 * MQ Broker故障降级机制 - 周期自检
 * 
 * @author jieli
 *
 */
// @Component
public class Pbit extends AbstractFallback {
	private ILog log = new Logger();
	private AtomicLong failureCounter = new AtomicLong(0); // 故障计数器
	private final int THRESHOLD_VALUE = 3; // 故障降级的阈值

	/** Scheduled */
	public void checkHealth() {
		try {
			// 周期自检MQ发布订阅消息功能

			fallbackSwitch.disable();
			failureCounter.set(0);
		} catch (Exception e) {
			log.error("PBIT", e);
			failureCounter.incrementAndGet();
		}

		if (failureCounter.get() >= THRESHOLD_VALUE) {
			fallbackSwitch.enable();
		}
	}
}
