package com.art1001.supply.quartz;

import org.quartz.*;

public interface QuartzService {


    /**
     * 修改任务JobDateMap
     *
     * @param cls  任务类
     * @param bJob 任务类属性
     * @return 是否修改成功
     */
    boolean modifyJobDateMap(Class<? extends Job> cls, MyJob bJob);

    /**
     * 添加定时任务
     *
     * @param cls  任务类
     * @param bJob 任务类属性
     * @return 是否添加成功
     */
    boolean addJobByCronTrigger(Class<? extends Job> cls, MyJob bJob);

    /**
     * 更改任务的执行时间
     * @param name 任务名称
     * @param group 触发器组
     * @param time cron表达式
     * @return
     */
    boolean modifyJobTime(String name, String group, String time) throws SchedulerException;

    /**
     * 从调度器中移除该任务
     * @param scheduler 调度器
     * @param triggerKey
     * @param jobKey
     */
    void removeJob(Scheduler scheduler, TriggerKey triggerKey, JobKey jobKey);

    /**
     * 获取调度器
     * @return
     */
    Scheduler getScheduler() throws SchedulerException;

    /**
     * 更新jobDataMap
     * @param jobName
     * @param jobGroup
     */
    void updateJobDataMap(String jobName, String jobGroup, String users) throws SchedulerException;
}
