package common.util.tcc;

/**
 * 抽象TCC任务
 * 
 * TCC模式(Try, Confirm, Cancel)是应用层的两阶段提交(2 Phase Commit)
 * 适用强隔离性、严格一致性要求的实时(非异步)业务
 * 
 * @author jieli
 * 
 * @param <U>
 *            request
 * @param <V>
 *            response
 */
public abstract class AbstractTccTask<U, V> {
	protected String name = "AbstractTccTask"; // 任务名

	public AbstractTccTask(String name) {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("name can NOT be null or empty");
		this.name = name;
	}

	/**
	 * Try尝试执行业务
	 * 
	 * 完成所有业务检查(一致性)，预留必需业务资源(准隔离性)。如订单状态置中间状态ING、冻结数据等
	 */
	public abstract boolean tryFor(U request);

	/**
	 * Confirm确认执行业务
	 * 
	 * 执行业务，不做任何业务检查，只使用Try预留的业务资源，需满足幂等性。如订单状态置已处理ED、冻结数据提交到业务数据等
	 */
	public abstract V confirm(U request);

	/**
	 * Cancel取消执行业务
	 * 
	 * 释放Try预留的业务资源或业务补偿(逆向操作)，需满足幂等性。如取消冻结数据等
	 */
	public abstract void cancel(U request);

	public String getName() {
		return name;
	}
}
