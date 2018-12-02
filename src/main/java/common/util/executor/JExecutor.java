package common.util.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import common.util.threadpool.JThreadPool;
import common.util.threadpool.ThreadPoolConstants;

/**
 * 执行器
 * 
 * @author jieli
 *
 */
public class JExecutor {
	private volatile static JExecutor _instance = null;
	private static ExecutorService es = JThreadPool.instance().getExecutorService();
	private static AtomicReference<ScheduledExecutorService> ses = new AtomicReference<>();

	public static JExecutor instance() {
		if (_instance == null) {
			synchronized (JExecutor.class) {
				if (_instance == null) {
					_instance = new JExecutor();
				}
			}
		}
		return _instance;
	}

	private JExecutor() {
		ses.set(Executors.newScheduledThreadPool(ThreadPoolConstants.N_THREADS_IO_INTENSIVE));
	}

	public <U, V> V execute(AbstractExecutorTask<U, V> task, U request) {
		V ret = null;
		Future<V> future = es.submit(new Callable<V>() {
			@Override
			public V call() throws Exception {
				return task.execute(request);
			}
		});
		try {
			ret = future.get(task.getTimeoutSeconds(), TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException e) {
			// e.printStackTrace();
			task.setFailed();
		} catch (TimeoutException e) {
			switch (task.getTimeoutPolicy()) {
			case AbstractExecutorTask.TERMINATED:
				task.setCompleted();
				break;
			case AbstractExecutorTask.RETRY:
				task.setFailed();
				break;
			}
		}

		if (task.getStatus() == AbstractExecutorTask.FAILED) {
			Long retryDelaySeconds = check4Retry(task);
			if (retryDelaySeconds > 0) {
				if (task.isAsync()) {
					ses.get().schedule(() -> {
						execute(task, request);
					}, retryDelaySeconds, TimeUnit.SECONDS);
				} else {
					execute(task, request);
				}
			}
		}
		return ret;
	}

	public <U, V> void delay(AbstractExecutorTask<U, V> task, U request) {
		long delaySeconds = task.getRetryDelaySeconds();
		if (task.isAsync() && delaySeconds > 0) {
			ses.get().schedule(() -> {
				task.execute(request);
			}, delaySeconds, TimeUnit.SECONDS);
		} else {
			task.execute(request);
		}
	}

	private <U, V> Long check4Retry(AbstractExecutorTask<U, V> task) {
		long ret = 0;
		long retriedCount = task.getRetriedCount();
		if (retriedCount < task.getRetryCount()) {
			task.setRetriedCount(retriedCount + 1);
			long retryDelaySeconds = task.getRetryDelaySeconds();
			if (retryDelaySeconds > 0) {
				// Retry Logic
				// retry... - but not immediately - put a delay...
				switch (task.getRetryLogic()) {
				case AbstractExecutorTask.RETRY_FIXED:
					break;
				case AbstractExecutorTask.RETRY_BACKOFF:
					retryDelaySeconds = retryDelaySeconds * (1 + task.getRetriedCount());
					break;
				}
			}
			ret = retryDelaySeconds;
		}
		return ret;
	}
}
