/**
 * 
 */
package com.art1001.supply.listener;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


/**
 * 系统启动时执行初始化任务
 *
 * @author wangyafeng
 * 2016年12月6日 下午4:21:41
 */
@Component
public class InitializationListener implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger logger = LoggerFactory.getLogger(InitializationListener.class);

	private SchedulerFactory schedulerFactory = new StdSchedulerFactory();;
	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (null == event.getApplicationContext().getParent()) {

			/**
			 * 这里是quartz任务调度器的启动  删了就不行了
			 */
			try {
				Scheduler scheduler = schedulerFactory.getScheduler();
				if(scheduler.isStarted()){
					logger.info("scheduler  xxxxx ");
				}else{
					scheduler.start();
				}
			} catch (SchedulerException e) {
				e.printStackTrace();
			}

			logger.info("System initialization success.");
		}
	}

}
