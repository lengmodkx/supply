package com.art1001.supply.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author heshaohua
 * @Title: Test
 * @Description: TODO
 * @date 2018/10/24 18:05
 **/
public class Test implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("这是测试的");
    }
}
