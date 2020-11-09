/**
 *
 */
package com.art1001.supply.listener;

import com.art1001.supply.entity.resource.ProResources;
import com.art1001.supply.entity.resource.ResourceEntity;
import com.art1001.supply.service.resource.ProResourcesService;
import com.art1001.supply.service.resource.ResourceService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 系统启动时执行初始化任务
 *
 * @author wangyafeng
 * 2016年12月6日 下午4:21:41
 */
@Component
public class InitializationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ResourceService resourceService;

    @Resource
    private ProResourcesService proResourcesService;


    private static final Logger logger = LoggerFactory.getLogger(InitializationListener.class);

    private SchedulerFactory schedulerFactory = new StdSchedulerFactory();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (null == event.getApplicationContext().getParent()) {

            List<String> allResources = redisUtil.getList(String.class, "allResources");
            redisUtil.lset("allResources", allResources);
            logger.info("所有资源key信息查询完毕,存储进redis.");


            /**
             * 这里是quartz任务调度器的启动  删了就不行了
             */
            try {
                Scheduler scheduler = schedulerFactory.getScheduler();
                if (scheduler.isStarted()) {
                    logger.info("scheduler  xxxxx ");
                } else {
                    scheduler.start();
                }
            } catch (SchedulerException e) {
                e.printStackTrace();
            }


            logger.info("System initialization success.");
        }
    }

}
