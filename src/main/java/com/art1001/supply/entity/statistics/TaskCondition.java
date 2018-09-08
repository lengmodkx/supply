package com.art1001.supply.entity.statistics;


import org.joda.time.DateTime;

/**
 * 模块
 *
 * @author lujing
 * @date 2018-09-05 17:08
 * @Description  统计页面查询条件
 */
public class TaskCondition {

    /*
    *根据任务数或其他条件
    **/

    private String taskNum;
    /*
    *根据成员查询
    **/

    private String taskMember;
    /*
    *根据任务分组
    **/

    private String taskGroupBy;
    /*
    *根据是否是子任务
    **/

    private int taskChild;
    /*
    *根据是否是回收站
    **/

    private int recycle;
    /*
    *根据天数，开始时天数和结束时天数
    **/

    private String startDay;
    private String endDay;
    /*
    *根据是否成功
    **/

    private String taskSucOrFail;

    public String getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(String taskNum) {
        this.taskNum = taskNum;
    }

    public String getTaskMember() {
        return taskMember;
    }

    public void setTaskMember(String taskMember) {
        this.taskMember = taskMember;
    }

    public String getTaskGroupBy() {
        return taskGroupBy;
    }

    public void setTaskGroupBy(String taskGroupBy) {
        this.taskGroupBy = taskGroupBy;
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


    public String getTaskSucOrFail() {
        return taskSucOrFail;
    }

    public void setTaskSucOrFail(String taskSucOrFail) {
        this.taskSucOrFail = taskSucOrFail;
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

    @Override
    public String toString() {
        return "TaskCondition{" +
                "taskNum='" + taskNum + '\'' +
                ", taskMember='" + taskMember + '\'' +
                ", taskGroupBy='" + taskGroupBy + '\'' +
                ", taskChild=" + taskChild +
                ", recycle=" + recycle +
                ", startDay=" + startDay +
                ", endDay=" + endDay +
                ", taskSucOrFail='" + taskSucOrFail + '\'' +
                '}';
    }
}
