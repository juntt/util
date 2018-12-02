package common.util.executor.parallel;

import java.util.concurrent.ExecutorService;

import common.util.executor.AbstractExecutorTask;
import common.util.threadpool.JThreadPool;

/**
 * 抽象并行任务
 * 
 * @author jieli
 *
 * @param <U>
 *            request
 * @param <V>
 *            response
 */
public abstract class AbstractParallelTask<U, V> extends AbstractExecutorTask<U, V> {
	protected static ExecutorService es = JThreadPool.instance().getExecutorService();

	public AbstractParallelTask() {
		super();
		isAsync = false;
	}

	/** 并行执行+合并结果集 */
	public V merge(U request) throws Exception {
		return async(request);
	}
}
