package common.util.mq;

import common.util.log.ILog;
import common.util.log.Logger;

/**
 * MQ Broker故障降级机制 - 抽象父类
 * 
 * @author jieli
 *
 */
public abstract class AbstractFallback {
	protected ILog log = new Logger();
	// === Bean ===
	protected IFallbackSwitch fallbackSwitch = null; // 降级开关bean
	protected IFallbackManager fallbackManager = null; // 管理器bean
}
