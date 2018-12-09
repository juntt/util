package common.util.tcc;

/**
 * TCC管理器接口
 * 
 * @author jieli
 *
 */
public interface ITccManager {
	/**
	 * 绑定业务和TxId
	 * 
	 * @param key
	 *            业务的唯一键
	 * @param timeout
	 *            超时时间(s)
	 * @return TxId(唯一性)
	 */
	String bind(String key, long timeout);

	/**
	 * 检查业务的唯一键是否已绑定
	 * 
	 * @param key
	 *            业务的唯一键
	 * @return
	 */
	boolean exist(String key);

	/**
	 * 解绑业务和TxId
	 * 
	 * @param txId
	 * @return
	 */
	boolean unbind(String txId);
}
