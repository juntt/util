package common.util.executor;

import java.util.Date;

public class DemoExecutorTask extends AbstractExecutorTask<Void, Void> {
	public DemoExecutorTask(String name) {
		super();
		this.name = name;
	}

	@Override
	protected void init() {
		loadPolicy(RETRY_POLICY);
	}

	@Override
	protected Void doExecute(Void request) {
		System.out.println(new Date() + " [" + name + "] 执行完成。已重试的次数=" + getRetriedCount());
		// 状态置失败
		setFailed();
		return null;
	}

	@Override
	protected Void recovery(Void request) {
		System.out.println(new Date() + " [" + name + "] 重试次数到达阈值，回调处理完成。已重试的次数=" + getRetriedCount());
		return null;
	}

	public static void main(String[] args) throws Exception {
		DemoExecutorTask demo1 = new DemoExecutorTask("demo1");
		System.out.println("同步执行");
		demo1.sync(null);
		DemoExecutorTask demo2 = new DemoExecutorTask("demo2");
		System.out.println("执行超时/失败后异步重试策略");
		demo2.async(null);
		DemoExecutorTask demo3 = new DemoExecutorTask("demo3");
		System.out.println("延迟异步执行");
		demo3.delay(null);
		Thread.currentThread();
		Thread.sleep(6000);
		System.out.println(demo1);
		System.out.println(demo2);
		System.out.println(demo3);
	}
}
