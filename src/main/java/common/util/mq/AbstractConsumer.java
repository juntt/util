package common.util.mq;

import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;

import common.util.tools.JsonUtils;

/**
 * MQ Broker故障降级机制 - 抽象消费者
 * 
 * @author jieli
 *
 * @param <U>
 *            消息载体
 * 
 */
public abstract class AbstractConsumer<U extends Serializable> extends AbstractFallback {
	// === MQ消息 ===
	protected String topic = null; // 消息主题
	protected Class<U> clazz = null; // 消息载体的Class对象
	// === Schedule ===
	protected long period = 60; // 周期(s)
	private AtomicReference<ScheduledExecutorService> ses = new AtomicReference<>();

	public AbstractConsumer() {
		init();
		ses.set(Executors.newSingleThreadScheduledExecutor());
		ses.get().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (!fallbackSwitch.isEnable() && fallbackManager.isEmpty(topic))
					return;

				execute();
			}
		}, period, period, TimeUnit.SECONDS);
	}

	/** 自定义初始化参数 */
	protected abstract void init();

	/**
	 * 自定义消费MQ消息
	 * 
	 * @param message
	 *            消息载体
	 */
	public abstract void consume(U message);

	private void execute() {
		while (!fallbackManager.isEmpty(topic)) {
			String json = fallbackManager.pop(topic);
			if (StringUtils.isNotEmpty(json)) {
				U message = JsonUtils.convert2T(json, clazz);
				consume(message);
			}
		}
	}
}
