package common.util.executor.parallel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DemoParallelTask extends AbstractParallelTask<Void, Integer> {
	public DemoParallelTask(String name) {
		super();
		this.name = name;
	}

	@Override
	protected void init() {
		loadPolicy(RETRY_POLICY);
	}

	@Override
	public Integer doExecute(Void request) {
		List<Future<Integer>> futureLst = new ArrayList<>();
		futureLst.add(es.submit(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return task();
			}

			private Integer task() {
				return 1;
			}
		}));
		futureLst.add(es.submit(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return task();
			}

			private Integer task() {
				return 2;
			}
		}));

		Integer ret = 0;
		try {
			for (Future<Integer> future : futureLst)
				ret += future.get(timeoutSeconds, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		System.out.println(new Date() + " [" + name + "] 执行完成。结果=" + ret);
		setFailed();
		return ret;
	}

	@Override
	protected Integer recovery(Void request) {
		System.out.println(new Date() + " [" + name + "] 重试次数到达阈值，回调处理完成。已重试的次数=" + getRetriedCount());
		return null;
	}

	public static void main(String[] args) throws Exception {
		DemoParallelTask demo = new DemoParallelTask("DemoParallelTask");
		demo.merge(null);
		Thread.sleep(6000);
		System.out.println(demo);
	}
}
