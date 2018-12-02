package common.util.log;

/**
 * Log接口
 * 
 * @author jieli
 *
 */
public interface ILog {
	void info(String title, String message);

	void infoPid(Object pid, String title, String message);

	void error(String title, String message);

	void error(String title, Throwable throwable);

	void errorPid(Object pid, String title, Throwable throwable);
}
