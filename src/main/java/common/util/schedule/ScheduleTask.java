package common.util.schedule;

import java.util.Calendar;
import java.util.Date;

import org.springframework.scheduling.support.CronSequenceGenerator;

/**
 * 计划任务
 * 
 * @author jieli
 *
 */
public class ScheduleTask {
	private String name; // 任务名，唯一标识符
	private long time; // 下次执行的UNIX时间戳
	private long period; // 间隔(s)

	/**
	 * 
	 * @param name
	 *            任务名，唯一标识符
	 * @param cron
	 *            Cron表达式
	 * @param period
	 *            间隔时间
	 * @param timeUnit
	 *            间隔时间单位
	 */
	public ScheduleTask(String name, String cron, long period, int timeUnit) {
		this.name = name;
		setTriggerTime(cron);
		switch (timeUnit) {
		case Calendar.SECOND:
			this.period = period;
			break;
		case Calendar.MINUTE:
			this.period = period * 60;
			break;
		case Calendar.HOUR:
			this.period = period * 60 * 60;
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	private void setTriggerTime(String cron) {
		Date now = new Date();
		CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cron);
		Date next = cronSequenceGenerator.next(now);
		this.time = next.getTime();
	}

	public String getName() {
		return name;
	}

	public long getTime() {
		return time;
	}

	public long getPeriod() {
		return period;
	}
}
