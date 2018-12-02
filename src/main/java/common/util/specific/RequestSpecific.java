package common.util.specific;

/**
 * 请求参数
 * 
 * @author jieli
 *
 */
public class RequestSpecific {
	/**
	 * 从请求参数数组中尝试解析业务ID
	 * 
	 * @param args
	 *            请求参数数组
	 * @return
	 */
	public static Object parsePid(Object[] args) {
		if (args != null && args.length > 0 && args[0] instanceof IRequestSpecific)
			return ((IRequestSpecific) args[0]).getPid();
		return null;
	}
}
