package com.art1001.supply.quartz;

import lombok.Data;
import org.quartz.JobDataMap;

@Data
public class MyJob {
    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 触发器组名称
     */
    private String triggerGroupName;

    /**
     * 任务组名称
     */
    private String jobGroupName;

    /**
     * 任务额外参数
     */
    private JobDataMap jobDataMap;

    /**
     * 时间规则
     */
    private String cronTime;
}
