package common.util.stereotype.enhancer;

import java.util.Date;

import common.util.stereotype.JLog;

public class DemoEnhancer extends AbstractEnhancer<String, Void> {
	@JLog
	@Override
	protected Void doExecute(String request) throws Exception {
		System.out.println(new Date() + "执行完成");
		throw new Exception();
	}

	public static void main(String[] args) throws Exception {
		DemoEnhancer demo = new DemoEnhancer();
		demo.execute("demo");
	}
}
