package common.util.tcc.demo;

import java.util.concurrent.ConcurrentHashMap;

import common.util.tcc.ITccManager;
import common.util.tcc.exception.TccException;

public class DemoTccManager implements ITccManager {
	private static ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

	@Override
	public String bind(String key, long timeout) {
		if (exist(key))
			throw new TccException("key: " + key + " exist");
		map.put(key, key);
		return key;
	}

	@Override
	public boolean unbind(String txId) {
		if (exist(txId))
			map.remove(txId);
		return true;
	}

	@Override
	public boolean exist(String key) {
		return map.contains(key);
	}
}
