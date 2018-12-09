package common.util.tcc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import common.util.log.ILog;
import common.util.log.Logger;
import common.util.tcc.exception.TccException;

/**
 * TCC执行器
 * 
 * 任务编排、执行业务(TRY失败自动补偿)
 * 
 * @author jieli
 *
 * @param <U>
 *            request
 * @param <V>
 *            response
 */
public class JTccExecutor<U, V> {
	private ILog log = new Logger();
	private ITccManager manager = null;
	private String txId = null; // 事务ID(唯一性)
	private List<TccEntity<U, V>> lst = new ArrayList<>(); // 编排的任务列表

	private static final int FAILED = -1;
	private static final int SUCCESS = 1;

	public JTccExecutor(ITccManager manager) {
		this.manager = manager;
	}

	/**
	 * 注册任务，按序编排
	 * 
	 * @param task
	 */
	public void register(AbstractTccTask<U, V> task) {
		if (task == null)
			throw new IllegalArgumentException("task can NOT be null");
		lst.add(new TccEntity<U, V>(task));
		log.info("TCC", String.format("register Task: {Name: %s} SUCCESS", task.getName()));
	}

	/**
	 * 执行业务
	 * 
	 * @param request
	 *            TCC请求
	 * @param key
	 *            业务的唯一键
	 * @param timeoutSeconds
	 *            超时时间(s)
	 * @return List<TCC响应> 业务实体集合中的所有TCC任务的响应
	 */
	public List<V> execute(U request, String key, long timeoutSeconds) throws TccException {
		if (lst == null || lst.isEmpty())
			throw new IllegalArgumentException("task list can NOT be null or empty, register task first");
		if (key == null || key.length() == 0)
			throw new IllegalArgumentException("key can NOT be null or empty");
		if (timeoutSeconds <= 0)
			throw new IllegalArgumentException("timeoutSeconds MUST > 0");

		try {
			tryFor(request, key, timeoutSeconds);
		} catch (TccException e) {
			cancel(request);
			throw e;
		}

		try {
			return confirm(request);
		} catch (Exception e) {
			// FIXME 异常处理如告警邮件等
			throw new TccException(e);
		}
	}

	private void tryFor(U request, String key, long timeout) throws TccException {
		bind(key, timeout);

		for (TccEntity<U, V> one : lst) {
			AbstractTccTask<U, V> task = one.getTask();
			try {
				if (task.tryFor(request)) {
					one.setTryStatus(SUCCESS);
					log.infoPid(txId, "TCC TRY", String.format("TRY Task: {Name: %s} SUCCESS", task.getName()));
				} else {
					one.setTryStatus(FAILED);
					log.infoPid(txId, "TCC TRY", String.format("TRY Task: {Name: %s} FAILED", task.getName()));
					throw new TccException(String.format("TRY Task: {Name: %s} FAILED", task.getName()));
				}
			} catch (Exception e) {
				log.infoPid(txId, "TCC TRY", String.format("TRY Task: {Name: %s} ERROR", task.getName()));
				log.errorPid(txId, "TCC TRY", e);
				throw new TccException(e);
			}
		}
	}

	private void cancel(U request) {
		try {
			for (TccEntity<U, V> one : lst) {
				AbstractTccTask<U, V> task = one.getTask();
				switch (one.getTryStatus()) {
				case FAILED:
					log.infoPid(txId, "TCC CANCEL", String.format("FIXME! Task: {Name: %s} TryStatus: FAILED", task.getName()));
					break;
				case SUCCESS:
					try {
						task.cancel(request);
						one.setCancelStatus(SUCCESS);
						log.infoPid(txId, "TCC CANCEL", String.format("CANCEL Task: {Name: %s} SUCCESS", task.getName()));
					} catch (Exception e) {
						one.setCancelStatus(FAILED);
						log.infoPid(txId, "TCC CANCEL", String.format("CANCEL Task: {Name: %s} ERROR", task.getName()));
						log.errorPid(txId, "TCC CANCEL", e);
						throw new TccException(e);
					}
					break;
				default:
					break;
				}
			}
		} finally {
			unbind();
		}
	}

	private void bind(String key, long timeout) throws TccException {
		if (manager.exist(key))
			throw new TccException(String.format("key: %s already bind", key));
		txId = manager.bind(key, timeout);
	}

	private void unbind() {
		if (txId != null) {
			manager.unbind(txId);
			txId = null;
		}
	}

	private List<V> confirm(U request) {
		try {
			List<V> ret = new ArrayList<>();
			for (TccEntity<U, V> one : lst) {
				AbstractTccTask<U, V> task = one.getTask();
				try {
					V tmp = (V) task.confirm(request);
					one.setConfirmStatus(SUCCESS);
					log.infoPid(txId, "TCC CONFIRM", String.format("CONFIRM Task: {Name: %s} SUCCESS", task.getName()));
					ret.add(tmp);
				} catch (Exception e) {
					one.setConfirmStatus(FAILED);
					log.infoPid(txId, "TCC CONFIRM", String.format("CONFIRM Task: {Name: %s} ERROR", task.getName()));
					log.errorPid(txId, "TCC CONFIRM", e);
					throw e;
				}
			}
			return ret;
		} finally {
			unbind();
		}
	}

	/** 事务ID(唯一性) */
	public String getTxId() {
		return txId;
	}

	@Override
	public String toString() {
		return String.format("TxId: %s, Task List: %s", txId, lst);
	}

	/** TCC业务实体 */
	protected class TccEntity<X, Y> {
		private AbstractTccTask<X, Y> task;
		private AtomicInteger tryStatus = new AtomicInteger(0);
		private AtomicInteger confirmStatus = new AtomicInteger(0);
		private AtomicInteger cancelStatus = new AtomicInteger(0);

		public TccEntity(AbstractTccTask<X, Y> task) {
			this.task = task;
		}

		public AbstractTccTask<X, Y> getTask() {
			return task;
		}

		public void setTryStatus(int status) {
			tryStatus.set(status);
		}

		public int getTryStatus() {
			return tryStatus.get();
		}

		public void setConfirmStatus(int status) {
			confirmStatus.set(status);
		}

		public int getConfirmStatus() {
			return confirmStatus.get();
		}

		public void setCancelStatus(int status) {
			cancelStatus.set(status);
		}

		public int getCancelStatus() {
			return cancelStatus.get();
		}

		@Override
		public String toString() {
			return String.format(
					"Task: {Name: %s}, TryStatus (-1-FAILED, 0-INIT, 1-SUCCESS): %d, ConfirmStatus: %d, CancelStatus: %d",
					task.getName(), tryStatus.get(), confirmStatus.get(), cancelStatus.get());
		}
	}
}
