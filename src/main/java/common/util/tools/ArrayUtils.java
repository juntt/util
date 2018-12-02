package common.util.tools;

import java.lang.reflect.Array;
import java.math.BigDecimal;

/**
 * Arrays工具集
 * 
 * @author jieli
 *
 */
public class ArrayUtils {
	/**
	 * 将T... 转换为Object来支持强制类型转换(T[]) args;
	 * 
	 * 替代Arrays.copyOf
	 * 
	 * @param arr
	 *            原数组对象
	 * @param trgtLen
	 *            新数组的长度
	 * @return
	 */
	public static Object copyOf(Object arr, int trgtLen) {
		Class clazz = arr.getClass(); // 原数组的类对象
		if (!clazz.isArray())
			return null;
		Class componentType = clazz.getComponentType(); // 原数组的元素类型
		int len = Array.getLength(arr);
		Object ret = Array.newInstance(componentType, trgtLen); // 反射构造新数组
		System.arraycopy(arr, 0, ret, 0, Math.min(len, trgtLen));
		return ret;
	}

	// === Quick Start ===
	private static <T> String md5(T... args) {
		T[] arr = (T[]) ArrayUtils.copyOf(args, args.length); // Arrays.copyOf
		String json = JsonUtils.convert2Json(arr);
		return EncryptUtils.md5Base64(json);
	}

	public static void main(String[] args) {
		System.out.println(md5("ctrip", 1998, 1000000L, BigDecimal.ONE));
	}
}
