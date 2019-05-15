package com.art1001.supply.quartz.impl;


import com.art1001.supply.quartz.MyJob;
import com.art1001.supply.quartz.QuartzService;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class QuartzServiceImpl implements QuartzService {

    /**
     * 调度器工厂
     */
    private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();;

    /**
     * 默认Job组名
     */
    private final String JOB_GROUP_NAME = "DEFAULT_JOB_GROUP_NAME";
    /**
     * 默认触发器组名
     */
    private final String TRIGGER_GROUP_NAME = "DEFAULT_TRIGGER_GROUP_NAME";
    /**
     * 修改任务JobDateMap
     *
     * @param cls  任务类
     * @param bJob 任务类属性
     * @return 是否修改成功
     */
    @Override
    public boolean modifyJobDateMap(Class<? extends Job> cls, MyJob bJob) {
        if (bJob == null) {
            return false;
        }

        String jobName = bJob.getJobName();
        if (StringUtils.isBlank(jobName)) {
            return false;
        }
        String triggerGroupName = bJob.getTriggerGroupName();
        if (StringUtils.isBlank(triggerGroupName)) {
            triggerGroupName = TRIGGER_GROUP_NAME;
        }
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, triggerGroupName);
        String jobGroupName = bJob.getJobGroupName();
        if (StringUtils.isBlank(jobGroupName)) {
            jobGroupName = JOB_GROUP_NAME;
        }
        try {
            Scheduler scheduler = getScheduler();
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            JobDetail jobDetail1 = scheduler.getJobDetail(jobKey);
            if (jobDetail1 == null) {
                return false;
            }
            JobDataMap oldJobDataMap = jobDetail1.getJobDataMap();
            JobDataMap jobDataMap = bJob.getJobDataMap();
            if (!oldJobDataMap.equals(jobDataMap)) {
                Class<? extends Job> jobClass = jobDetail1.getJobClass();
                removeJob(scheduler, triggerKey, jobKey);
                return addJobByCronTrigger(scheduler, jobClass, jobName, jobGroupName, triggerGroupName, bJob.getCronTime(), jobDataMap);
            }
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新quartz
     * @param users
     * @param jobName
     * @param jobGroup
     */
    @Override
    public void updateJobDataMap(String jobName, String jobGroup, String users) throws SchedulerException {
        getScheduler().getJobDetail(new JobKey(jobName, jobGroup)).getJobDataMap().put("users",users);
    }

    /**
     * 修改某个任务的执行时间
     * @param name
     * @param group
     * @param time
     * @return
     * @throws SchedulerException
     */
    @Override
    public boolean modifyJobTime(String name, String group, String time) throws SchedulerException {
        Scheduler scheduler = getScheduler();
        Date date = null;
        TriggerKey triggerKey = new TriggerKey(name, group);
        CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        String oldTime = cronTrigger.getCronExpression();
        if (!oldTime.equalsIgnoreCase(time)) {
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(time);
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name, group)
                    .withSchedule(cronScheduleBuilder).build();
            date = scheduler.rescheduleJob(triggerKey, trigger);
        }
        return date != null;
    }


    /**
     * 添加定时任务
     *
     * @param cls  任务类
     * @param bJob 任务类属性
     * @return 是否添加成功
     */
    @Override
    public boolean addJobByCronTrigger(Class<? extends Job> cls, MyJob bJob) {
        if (bJob == null) {
            return false;
        }
        String jobName = bJob.getJobName();
        if (StringUtils.isBlank(jobName)) {
            return false;
        }
        try {
            Scheduler scheduler = getScheduler();
            return setJobDetailAndCronTriggerInScheduler(cls, jobName, bJob.getJobGroupName(), bJob.getTriggerGroupName(), bJob.getCronTime(), bJob.getJobDataMap(), scheduler);
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 使用CronTrigger类型添加任务
     *
     * @param scheduler        调度器
     * @param cls              任务类
     * @param jobName          任务名
     * @param jobGroupName     任务组名（为空使用默认）
     * @param triggerGroupName 触发器组名（为空使用默认）
     * @param time             crond格式时间
     * @param jobDataMap       附带参数
     * @return 是否添加成功
     */
    private boolean addJobByCronTrigger(Scheduler scheduler, Class<? extends Job> cls, String jobName, String jobGroupName,
                                               String triggerGroupName, String time, JobDataMap jobDataMap) {
        try {
            return setJobDetailAndCronTriggerInScheduler(cls, jobName, jobGroupName, triggerGroupName, time, jobDataMap, scheduler);
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取调度器
     *
     * @return Scheduler
     * @throws SchedulerException Scheduler获取异常
     */
    @Override
    public Scheduler getScheduler() throws SchedulerException {
        return schedulerFactory.getScheduler();
    }
    /**
     * 获取CronTrigger
     * @param jobName          任务名
     * @param triggerGroupName 触发器组名（为空使用默认）
     * @param time             crond格式时间
     * @return CronTrigger
     */
    private CronTrigger getCronTrigger(String jobName, String triggerGroupName, String time) {
        if (StringUtils.isBlank(triggerGroupName)) {
            triggerGroupName = TRIGGER_GROUP_NAME;
        }
        return TriggerBuilder.newTrigger().withIdentity(jobName, triggerGroupName)
                .withSchedule(CronScheduleBuilder.cronSchedule(time)).build();
    }


    /**
     * 获取JobDetail
     *
     * @param jobName      任务名
     * @param jobGroupName 任务组名（为空使用默认）
     * @param cls          任务类
     * @param jobDataMap   附带参数
     * @return JobDetail
     */
    private JobDetail getJobDetail(String jobName, String jobGroupName, Class<? extends Job> cls, JobDataMap jobDataMap) {
        if (StringUtils.isBlank(jobGroupName)) {
            jobGroupName = JOB_GROUP_NAME;
        }

        if (jobDataMap != null) {
            return JobBuilder.newJob(cls).withIdentity(jobName, jobGroupName).usingJobData(jobDataMap).build();
        } else {
            return JobBuilder.newJob(cls).withIdentity(jobName, jobGroupName).build();
        }
    }

    /**
     * 从调度器中移除Job
     * @param scheduler  调度器
     * @param triggerKey 触发器key（名，组）
     * @param jobKey     任务key（名，组）
     */
    @Override
    public void removeJob(Scheduler scheduler, TriggerKey triggerKey, JobKey jobKey) {
        try {
            // 停止触发器
            scheduler.pauseTrigger(triggerKey);
            //移除触发器
            scheduler.unscheduleJob(triggerKey);
            // 删除任务
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    /**
     * 设置JobDetail 和 CronTrigger 到 scheduler（已获取的调度器中，无需重复调用）
     *
     * @param cls              任务类
     * @param jobName          任务名
     * @param jobGroupName     任务组名（为空使用默认）
     * @param triggerGroupName 触发器组名（为空使用默认）
     * @param time             crond格式时间
     * @param jobDataMap       附带参数
     * @param scheduler        调度器
     * @return 设置成功与否
     * @throws SchedulerException 调度器异常
     */
    private boolean setJobDetailAndCronTriggerInScheduler(Class<? extends Job> cls, String jobName, String jobGroupName, String triggerGroupName,
                                                                 String time, JobDataMap jobDataMap, Scheduler scheduler) throws SchedulerException {
        if (!isJobKey(scheduler, jobName, jobGroupName)) {
            return false;
        }
        JobDetail jobDetail = getJobDetail(jobName, jobGroupName, cls, jobDataMap);
        CronTrigger trigger = getCronTrigger(jobName, triggerGroupName, time);
        scheduler.scheduleJob(jobDetail, trigger);
        if (!scheduler.isShutdown()) {
            scheduler.start();
        }
        return true;
    }

    /**
     * 判断是否存在JobKey
     *
     * @param scheduler    任务调度器
     * @param jobName      任务名
     * @param jobGroupName 任务组名
     * @return 是否存在JobKey
     */
    private boolean isJobKey(Scheduler scheduler, String jobName, String jobGroupName) {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            return jobDetail == null;
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }
}
