package common.util.mq;

import java.io.Serializable;

import common.util.tools.JsonUtils;

/**
 * MQ Broker故障降级机制 - 抽象发布者
 * 
 * @author jieli
 *
 * @param <U>
 *            消息载体
 * 
 */
public abstract class AbstractProducer<U extends Serializable> extends AbstractFallback {
	// === MQ消息 ===
	protected String topic = null; // 消息主题

	public AbstractProducer() {
		init();
	}

	/** 自定义初始化参数 */
	protected abstract void init();

	/**
	 * 创建并发布MQ消息
	 * 
	 * @param message
	 *            消息载体
	 */
	public abstract void publish(U message);

	/**
	 * MQ故障时自动降级，故障恢复时自动切换为publish()
	 * 
	 * @param message
	 *            消息载体
	 */
	public void publishFallback(U message) {
		if (fallbackSwitch.isEnable()) {
			fallbackManager.register(topic);
			fallbackManager.push(topic, JsonUtils.convert2Json(message));
		} else {
			publish(message);
		}
	}
}
