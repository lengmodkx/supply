package com.art1001.supply.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author heshaohua
 * @Title: RepeatJob
 * @Description: TODO
 * @date 2018/10/24 15:16
 **/
public class RepeatJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("第二个任务");
    }
}
