package common.util.schedule;

import java.util.Calendar;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import common.util.cache.IRedis;
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
	// @Autowired
	private IRedis redis;

	private static final String CRON_1M = "0 0/1 * * * ? "; // period=1min

	@Scheduled(cron = CRON_1M)
	public void heartbeatSchedule() {
		ScheduleTask heartbeatTask = new ScheduleTask("demoSchedule", CRON_1M, 1, Calendar.MINUTE);
		// 集群幂等性
		String key = heartbeatTask.getName() + heartbeatTask.getTime();
		String value = "@" + System.currentTimeMillis();
		if (!redis.tryLock(key, 30, value))
			return;

		try {
			// do sth.
		} catch (Exception e) {
			log.error("heartbeatSchedule", e);
		}
		redis.unLock(key, value);
	}
}
