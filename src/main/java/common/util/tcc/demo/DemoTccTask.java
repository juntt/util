package common.util.tcc.demo;

import common.util.tcc.AbstractTccTask;

public class DemoTccTask extends AbstractTccTask<Integer, Boolean> {
	public static int available = 10; // 可用库存
	public static int frozen = 0; // 冻结库存

	public DemoTccTask(String name) {
		super(name);
	}

	@Override
	public boolean tryFor(Integer request) {
		if ((available - request) < 0)
			return false;
		available -= request;
		frozen += request;
		System.out.println(String.format("Task: {Name: %s} available: %d, frozen: %d", name, available, frozen));
		return true;
	}

	@Override
	public Boolean confirm(Integer request) {
		Boolean ret = new Boolean(true);
		frozen -= request;
		System.out.println(String.format("Task: {Name: %s} available: %d, frozen: %d", name, available, frozen));
		return ret;
	}

	@Override
	public void cancel(Integer request) {
		frozen -= request;
		available += request;
		System.out.println(String.format("Task: {Name: %s} available: %d, frozen: %d", name, available, frozen));
	}
}
