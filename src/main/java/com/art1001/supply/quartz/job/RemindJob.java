package com.art1001.supply.quartz.job;

import com.art1001.supply.service.quartz.QuartzInfoService;
import com.art1001.supply.service.task.TaskRemindRuleService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author heshaohua
 * @Title: RemindJob
 * @Description: TODO
 * @date 2018/10/23 10:44
 **/
public class RemindJob implements Job {

    @Resource
    private TaskRemindRuleService taskRemindRuleService;

    @Resource
    private QuartzInfoService quartzInfoService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("任务开始:\t" + "提醒:\t"+ jobExecutionContext.getMergedJobDataMap().get("users"));
    }
}
