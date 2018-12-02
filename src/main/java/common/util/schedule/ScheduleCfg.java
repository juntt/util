package common.util.schedule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import common.util.threadpool.ThreadPoolConstants;

/**
 * Spring Schedule配置
 * 
 * @author jieli
 *
 */
@Configuration
@EnableScheduling
public class ScheduleCfg implements SchedulingConfigurer {
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(ThreadPoolConstants.N_THREADS_IO_INTENSIVE);
		taskRegistrar.setScheduler(ses);
	}
}
