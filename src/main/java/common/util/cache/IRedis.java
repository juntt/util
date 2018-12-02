package common.util.cache;

import java.util.List;

/**
 * Redis接口
 * 
 * @author jielib
 *
 */
public interface IRedis {
	// === Lock ===
	/**
	 * SETNX(key, value); EXPIRE(key, seconds);
	 * 
	 * @param key
	 * @param seconds
	 * @param value
	 * @return
	 */
	Boolean tryLock(String key, long seconds, String value);

	/**
	 * DEL(key);
	 * 
	 * @param key
	 */
	void unLock(String key);

	// === Cache ===
	/**
	 * SETEX(key, seconds, value); SADD(setKey, key); 将key添加到缓存set
	 * 
	 * @param key
	 * @param seconds
	 *            生存时间(s)
	 * @param value
	 * @param setKey
	 * @return
	 */
	<T> Boolean save(String key, int seconds, T value, String setKey);

	/**
	 * GET(key);
	 * 
	 * @param key
	 * @param clazz
	 * @return
	 */
	<T> T load(String key, Class<T> clazz);

	/**
	 * 返回缓存的列表
	 * 
	 * @param key
	 * @param clazz
	 * @return
	 */
	<T> List<T> loadList(String key, Class<T> clazz);

	/**
	 * 遍历SMEMBERS(setKey)中的key：DEL(key); SREM(setKey, key); 删除值并从缓存set中移除key
	 * 
	 * @param setKey
	 *            缓存set
	 */
	void clearAll(String setKey);
}
