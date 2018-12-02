package common.util.mq;

import java.util.Set;

/**
 * MQ Broker故障降级机制 - 管理器
 * 
 * @author jieli
 *
 */
public interface IFallbackManager {
	// === Set<Topic> ===
	/** 注册MQ主题(降级后发布的消息) */
	void register(String topic);

	/** 返回已注册的MQ主题集 */
	Set<String> topics();

	// === FIFO key: topic value: List ===
	/** 返回topic对应的FIFO是否为空队列 */
	boolean isEmpty(String topic);

	/** push json到topic对应的FIFO */
	void push(String topic, String json);

	/** 从topic对应的FIFO pop json */
	String pop(String topic);
}
