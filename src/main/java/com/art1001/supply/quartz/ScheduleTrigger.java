package com.art1001.supply.quartz;

import lombok.Data;

/**
 * @author heshaohua
 * @Title: ScheduleTrigger
 * @Description: TODO
 * @date 2018/10/22 18:04
 **/
@Data
public class ScheduleTrigger {

    private Long id;

    /**
     * 时间表达式
     */
    private String cron;

    /**
     * 使用状态 0：禁用   1：启用
     */
    private String status;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务分组
     */
    private String jobGroup;
}