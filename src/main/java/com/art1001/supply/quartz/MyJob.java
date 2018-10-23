package com.art1001.supply.quartz;

import lombok.Data;
import org.quartz.JobDataMap;

@Data
public class MyJob {

    private String jobName;

    private String triggerGroupName;

    private String jobGroupName;

    private JobDataMap jobDataMap;

    private String cronTime;
}
