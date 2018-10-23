package com.art1001.supply.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author heshaohua
 * @Title: RemindJob
 * @Description: TODO
 * @date 2018/10/23 10:44
 **/
public class RemindJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("任务开始:\t" + "提醒:\t"+ jobExecutionContext.getMergedJobDataMap().get("name"));
    }
}
