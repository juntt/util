package common.util.tools.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 持有Spring应用上下文
 * 
 * @author jieli
 *
 */
@Component
public class JApplicationContextAware implements ApplicationContextAware {
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// 持有Spring应用上下文
		ContextUtils.setContext(applicationContext);
	}
}
