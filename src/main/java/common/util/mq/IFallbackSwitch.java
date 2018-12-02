package common.util.mq;

/**
 * MQ Broker故障降级机制 - 降级开关接口
 * 
 * @author jieli
 *
 */
public interface IFallbackSwitch {
	/** 降级开关是否已开启 */
	boolean isEnable();

	/** 开启 */
	void enable();

	/** 关闭 */
	void disable();
}
