package common.util.log;

import java.util.Date;

import common.util.tools.TimeUtils;

/**
 * Logger
 * 
 * @author jieli
 *
 */
public class Logger implements ILog {
	@Override
	public void info(String title, String message) {
		System.out.println(TimeUtils.convert2String(new Date(), null) + " INFO " + title + " - " + message);
	}

	@Override
	public void infoPid(Object pid, String title, String message) {
		System.out
				.println(TimeUtils.convert2String(new Date(), null) + " INFO [" + pid + "] " + title + " - " + message);
	}

	@Override
	public void error(String title, String message) {
		System.out.println(TimeUtils.convert2String(new Date(), null) + " ERROR " + title + " - " + message);
	}

	@Override
	public void error(String title, Throwable throwable) {
		System.out.println(TimeUtils.convert2String(new Date(), null) + " ERROR " + title + " - error: ");
		throwable.printStackTrace();
	}

	@Override
	public void errorPid(Object pid, String title, Throwable throwable) {
		System.out.println(TimeUtils.convert2String(new Date(), null) + " ERROR [" + pid + "] " + title + " - error: ");
		throwable.printStackTrace();
	}
}
