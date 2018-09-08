package com.art1001.supply.entity.statistics;


import org.joda.time.DateTime;

/**
 * 模块
 *
 * @author lujing
 * @date 2018-09-05 17:08
 * @Description  统计页面查询条件
 */
public class StatisticsDTO {

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
    private int taskChild;

    /**
     *根据是否是回收站
     */
    private int recycle;

    /**
     *根据天数，开始时天数和结束时天数
     */
    private String startDay;

    private String endDay;

    /**
     *根据是否成功
     */
    private String taskCase;


    public String getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(String taskCount) {
        this.taskCount = taskCount;
    }

    public String getTaskMember() {
        return taskMember;
    }

    public void setTaskMember(String taskMember) {
        this.taskMember = taskMember;
    }

    public String getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
    }

    public int getTaskChild() {
        return taskChild;
    }

    public void setTaskChild(int taskChild) {
        this.taskChild = taskChild;
    }

    public int getRecycle() {
        return recycle;
    }

    public void setRecycle(int recycle) {
        this.recycle = recycle;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public String getTaskCase() {
        return taskCase;
    }

    public void setTaskCase(String taskCase) {
        this.taskCase = taskCase;
    }

    @Override
    public String toString() {
        return "StatisticsDTO{" +
                "taskCount='" + taskCount + '\'' +
                ", taskMember='" + taskMember + '\'' +
                ", taskGroup='" + taskGroup + '\'' +
                ", taskChild=" + taskChild +
                ", recycle=" + recycle +
                ", startDay='" + startDay + '\'' +
                ", endDay='" + endDay + '\'' +
                ", taskCase='" + taskCase + '\'' +
                '}';
    }
}
