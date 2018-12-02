package common.util.tcc.demo;

import java.util.List;

import common.util.tcc.JTccExecutor;
import common.util.tcc.exception.TccException;

public class DemoBusiness {
	private DemoTccManager manager = new DemoTccManager();

	private JTccExecutor<Integer, Boolean> newJccExecutor() {
		JTccExecutor<Integer, Boolean> executor = new JTccExecutor<Integer, Boolean>(manager);
		executor.register(new DemoTccTask("DemoTccTask1"));
		executor.register(new DemoTccTask("DemoTccTask2"));
		return executor;
	}

	public void business(int amount) {
		JTccExecutor<Integer, Boolean> executor = newJccExecutor();
		try {
			List<Boolean> ret = executor.execute(amount, "DemoBusiness", 10);
			System.out.println("ret: " + ret);
		} catch (TccException e) {
			e.printStackTrace();
		}
		System.out.println(String.format("available: %d, frozen: %d", DemoTccTask.available, DemoTccTask.frozen));
		System.out.println(String.format("TccExecutor: {%s}", executor));
	}

	public static void main(String[] args) {
		new DemoBusiness().business(6);
	}
}
