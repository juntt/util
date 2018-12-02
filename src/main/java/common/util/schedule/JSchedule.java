package common.util.schedule;

import java.util.Calendar;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import common.util.log.ILog;
import common.util.log.Logger;

/**
 * Spring Schedule
 * 
 * @author jieli
 *
 */
@Component
public class JSchedule {
	private ILog log = new Logger();

	private static final String CRON_1M = "0 0/1 * * * ? "; // period=1min

	@Scheduled(cron = CRON_1M)
	public void heartbeatSchedule() {
		ScheduleTask heartbeatTask = new ScheduleTask("demoSchedule", CRON_1M, 1, Calendar.MINUTE);
		// 集群幂等性
		String key = heartbeatTask.getName() + heartbeatTask.getTime();
		try {
			// try lock
			log.info("heartbeat", "begin");
			// do sth.
		} catch (Exception e) {
			log.error("heartbeat", e);
		} finally {
			// unlock
			log.info("heartbeat", "end");
		}
	}
}
