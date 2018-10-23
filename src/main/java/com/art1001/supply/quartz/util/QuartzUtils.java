package com.art1001.supply.quartz.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author heshaohua
 * @Title: QuartzUtils
 * @Description: TODO
 * @date 2018/10/23 10:31
 **/
@Slf4j
public class QuartzUtils {
    /**
     * 调度器工厂
     */
    private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();
    /**
     * 默认Job组名
     */
    private static String JOB_GROUP_NAME = "DEFAULT_JOB_GROUP_NAME";
    /**
     * 默认触发器组名
     */
    private static String TRIGGER_GROUP_NAME = "DEFAULT_TRIGGER_GROUP_NAME";

    /**
     * 获取调度器
     *
     * @return Scheduler
     * @throws SchedulerException Scheduler获取异常
     */
    private static Scheduler getScheduler() throws SchedulerException {
        return schedulerFactory.getScheduler();
    }

    /**
     * 获取CronTrigger
     *
     * @param jobName          任务名
     * @param triggerGroupName 触发器组名（为空使用默认）
     * @param time             crond格式时间
     * @return CronTrigger
     */
    private static CronTrigger getCronTrigger(String jobName, String triggerGroupName, String time) {
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
    private static JobDetail getJobDetail(String jobName, String jobGroupName, Class<? extends Job> cls, JobDataMap jobDataMap) {
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
    private static boolean setJobDetailAndCronTriggerInScheduler(Class<? extends Job> cls, String jobName, String jobGroupName, String triggerGroupName,
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
     * 从调度器中移除Job
     *
     * @param scheduler  调度器
     * @param triggerKey 触发器key（名，组）
     * @param jobKey     任务key（名，组）
     */
    private static void removeJob(Scheduler scheduler, TriggerKey triggerKey, JobKey jobKey) {
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
     * 使用CronTrigger类型添加任务
     *
     * @param scheduler        调度器
     * @param cls              任务嘞
     * @param jobName          任务名
     * @param jobGroupName     任务组名（为空使用默认）
     * @param triggerGroupName 触发器组名（为空使用默认）
     * @param time             crond格式时间
     * @param jobDataMap       附带参数
     * @return 是否添加成功
     */
    private static boolean addJobByCronTrigger(Scheduler scheduler, Class<? extends Job> cls, String jobName, String jobGroupName,
                                               String triggerGroupName, String time, JobDataMap jobDataMap) {
        try {
            return setJobDetailAndCronTriggerInScheduler(cls, jobName, jobGroupName, triggerGroupName, time, jobDataMap, scheduler);
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断是否存在JobKey
     *
     * @param scheduler    任务调度器
     * @param jobName      任务名
     * @param jobGroupName 任务组名
     * @return 是否存在JobKey
     */
    private static boolean isJobKey(Scheduler scheduler, String jobName, String jobGroupName) {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            return jobDetail == null;
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 添加定时任务
     *
     * @param cls              任务类
     * @param jobName          任务名
     * @param jobGroupName     任务组名（为空使用默认）
     * @param triggerGroupName 触发器组名（为空使用默认）
     * @param time             crond格式时间
     * @param jobDataMap       附带参数
     * @return 是否正常添加任务
     */
    public static boolean addJobByCronTrigger(Class<? extends Job> cls, String jobName, String jobGroupName,
                                              String triggerGroupName, String time, JobDataMap jobDataMap) {
        try {
            if (StringUtils.isBlank(jobName)) {
                return false;
            }
            Scheduler scheduler = getScheduler();
            return setJobDetailAndCronTriggerInScheduler(cls, jobName, jobGroupName, triggerGroupName, time, jobDataMap, scheduler);
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 修改任务时间
     *
     * @param jobName          任务名
     * @param time             crond格式时间
     * @param jobGroupName     任务组名（为空使用默认）
     * @param triggerGroupName 触发器组名（为空使用默认）
     * @param jobDataMap       附带参数
     * @return 是否修改成功
     */
    public static boolean modifyJobTime(String jobName, String time, String jobGroupName,
                                        String triggerGroupName, JobDataMap jobDataMap) {
        try {
            if (StringUtils.isBlank(jobName)) {
                return false;
            }
            Scheduler scheduler = getScheduler();
            if (StringUtils.isBlank(triggerGroupName)) {
                triggerGroupName = TRIGGER_GROUP_NAME;
            }
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, triggerGroupName);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                return false;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(time)) {
                if (StringUtils.isBlank(jobGroupName)) {
                    jobGroupName = JOB_GROUP_NAME;
                }
                JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                Class<? extends Job> jobClass = jobDetail.getJobClass();
                removeJob(scheduler, triggerKey, jobKey);
                return addJobByCronTrigger(scheduler, jobClass, jobName, jobGroupName, triggerGroupName, time, jobDataMap);
            }
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 启动所有定时任务
     */
    public static void startJobs() {
        try {
            Scheduler scheduler = getScheduler();
            scheduler.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 关闭所有定时任务
     */
    public static void shutdownJobs() {
        try {
            Scheduler scheduler = getScheduler();
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 停止一个job任务
     *
     * @param jobName      任务名
     * @param jobGroupName 任务组名（空位默认）
     * @return 是否停止
     */
    public static boolean pauseJob(String jobName, String jobGroupName) {
        try {
            Scheduler scheduler = getScheduler();
            if (StringUtils.isBlank(jobGroupName)) {
                jobGroupName = JOB_GROUP_NAME;
            }
            scheduler.interrupt(JobKey.jobKey(jobName, jobGroupName));
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 恢复一个job任务
     *
     * @param jobName      任务名
     * @param jobGroupName 任务组名（空位默认）
     * @return 是否恢复
     */
    public static boolean resumeJob(String jobName, String jobGroupName) {
        try {
            Scheduler scheduler = getScheduler();
            scheduler.resumeJob(JobKey.jobKey(jobName, jobGroupName));
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

//    /**
//     * 添加定时任务
//     *
//     * @param cls  任务类
//     * @param bJob 任务类属性
//     * @return 是否添加成功
//     */
//    public static boolean addJobByCronTrigger(Class<? extends Job> cls, BaseJob bJob) {
//        if (bJob == null) {
//            return false;
//        }
//        String jobName = bJob.getJobName();
//        if (StringUtils.isBlank(jobName)) {
//            return false;
//        }
//        try {
//            Scheduler scheduler = getScheduler();
//            return setJobDetailAndCronTriggerInScheduler(cls, jobName, bJob.getJobGroupName(), bJob.getTriggerGroupName(), bJob.getCronTime(), bJob.getJobDataMap(), scheduler);
//        } catch (SchedulerException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    /**
//     * 修改任务JobDateMap
//     *
//     * @param cls  任务类
//     * @param bJob 任务类属性
//     * @return 是否修改成功
//     */
//    public static boolean modifyJobDateMap(Class<? extends Job> cls, BaseJob bJob) {
//        if (bJob == null) {
//            return false;
//        }
//        String jobName = bJob.getJobName();
//        if (StringUtils.isBlank(jobName)) {
//            return false;
//        }
//        String triggerGroupName = bJob.getTriggerGroupName();
//        if (StringUtils.isBlank(triggerGroupName)) {
//            triggerGroupName = TRIGGER_GROUP_NAME;
//        }
//        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, triggerGroupName);
//        String jobGroupName = bJob.getJobGroupName();
//        if (StringUtils.isBlank(jobGroupName)) {
//            jobGroupName = JOB_GROUP_NAME;
//        }
//        try {
//            Scheduler scheduler = getScheduler();
//            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
//            JobDetail jobDetail1 = scheduler.getJobDetail(jobKey);
//            if (jobDetail1 == null) {
//                return false;
//            }
//            JobDataMap oldJobDataMap = jobDetail1.getJobDataMap();
//            JobDataMap jobDataMap = bJob.getJobDataMap();
//            if (!oldJobDataMap.equals(jobDataMap)) {
//                Class<? extends Job> jobClass = jobDetail1.getJobClass();
//                removeJob(scheduler, triggerKey, jobKey);
//                return addJobByCronTrigger(scheduler, jobClass, jobName, jobGroupName, triggerGroupName, bJob.getCronTime(), jobDataMap);
//            }
//            return true;
//        } catch (SchedulerException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
}
