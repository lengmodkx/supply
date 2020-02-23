package com.art1001.supply.entity.statistics;

import lombok.Data;

/**
 * @Auther: Administrator
 * @Date: 2019/4/23 15:32
 * @Description:
 */
@Data
public class StaticDto {


    /**
     *根据任务数  \ 工时  \  Story Points
     */
    private String taskCount;

    /**
     *根据成员查询
     */
    private String taskMember;

    /**
     *根据任务分组
     */
    private String taskGroup;

    /**
     *根据是否是子任务
     */
    private String taskChild;

    public String getTaskChild() {
        return taskChild;
    }

    public void setTaskChild(String taskChild) {
        this.taskChild = taskChild;
    }

    public String getTaskRecycle() {
        return taskRecycle;
    }

    public void setTaskRecycle(String taskRecycle) {
        this.taskRecycle = taskRecycle;
    }

    /**
     *根据是否是回收站
     */
    private String taskRecycle;

    /**
     *根据天数，开始时天数和结束时天数
     */
    private String startDay;

    private String endDay;

    /**
     *根据是否成功
     */
    private String taskCase;

    /**
     *  0 未完成  1 已完成
     */
    private Integer taskCondition;

    /**
     *根据天数
     */
    private  int  DayNum;

    /**
     *根据天数
     */
    private  String  taskDay;



}
