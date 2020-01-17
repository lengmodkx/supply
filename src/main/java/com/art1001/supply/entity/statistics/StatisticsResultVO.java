package com.art1001.supply.entity.statistics;


import lombok.Data;

/**
 * 统计页面的总量概览详细数据实体类
 *
 * @author lujing
 * @date 2018-09-05 10:04
 */
@Data
public class StatisticsResultVO {

    /**
     *任务创建时间
     */

    private String createTime;

    /**
     *任务完成时间
     */

    private String finishTime;

    /**
     *任务截至时间
     */

    private String endTime;


    /**
     *任务是否完成
     */
    private String taskCase;


    /**
     *任务名称
     */
    private String taskName;

    /**
     *执行者
     */
    private String executor;

    /**
     *任务分组
     */
    private String taskGroup;

    /**
     *列表
     */
    private String listView;

    /**
     *逾期天数
     */
    private int overdueNum;

    /**
     *动态数
     */
    private int dynamicNum;

    /**
     *变动类型
     */
    private String changeType;

    /**
     *任务数
     */
    private String taskCountString;

    /**
     *任务数
     */
    private int taskCountInt;

    /**
     *平均任务数
     */
    private double taskCountDouble;

    /**
     *累计完成任务数
     */
    private int taskCountAdd;

    /**
     *剩余任务数
     */
    private int taskCountinus;

    /**
     *完成天数
     */
    private String taskDayNum;

    /**
     *总完成天数
     */
    private String taskDayGross;

    /**
     *未完成任务数
     */
    private int unfinishTaskNum;

    /**
     *已完成任务数
     */
    private int finishTaskNum;

    /**
     *优先级
     */
    private String taskPrecedence;

}
