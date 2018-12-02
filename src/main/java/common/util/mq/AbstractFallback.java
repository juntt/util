package common.util.mq;

/**
 * MQ Broker故障降级机制 - 抽象父类
 * 
 * @author jieli
 *
 */
public abstract class AbstractFallback {
	// === Bean ===
	protected IFallbackSwitch fallbackSwitch = null; // 降级开关bean
	protected IFallbackManager fallbackManager = null; // 管理器bean
}
