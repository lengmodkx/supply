package com.art1001.supply.entity.statistics;

/**
 * 模块
 *
 * @author lujing
 * @date 2018-09-05 11:02
 * @Description  统计页面数据实体类
 */
public class TaskDistribution {

    /**
     *任务是否完成
     */
    private String taskCase;

    /**
     *任务数
     */
    private int taskNum;

    /**
     *任务分组
     */
    private String listView;

    /**
     *未完成任务数
     */
    private int taskBad;

    /**
     *已完成任务数
     */
    private int taskSuccess;

    /**
     *执行者
     */
    private String executor;

    /**
     *优先级
     */
    private String taskPrecedence;

    /**
     *任务名称
     */
    private String taskName;

    // 封装信息

    public String getTaskCase() {
        return taskCase;
    }

    public void setTaskCase(String taskCase) {
        this.taskCase = taskCase;
    }

    public int getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }

    public String getListView() {
        return listView;
    }

    public void setListView(String listView) {
        this.listView = listView;
    }

    public int getTaskBad() {
        return taskBad;
    }

    public void setTaskBad(int taskBad) {
        this.taskBad = taskBad;
    }

    public int getTaskSuccess() {
        return taskSuccess;
    }

    public void setTaskSuccess(int taskSuccess) {
        this.taskSuccess = taskSuccess;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public String getTaskPrecedence() {
        return taskPrecedence;
    }

    public void setTaskPrecedence(String taskPrecedence) {
        this.taskPrecedence = taskPrecedence;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }


    @Override
    public String toString() {
        return "TaskDistribution{" +
                "taskCase='" + taskCase + '\'' +
                ", taskNum=" + taskNum +
                ", listView='" + listView + '\'' +
                ", taskBad=" + taskBad +
                ", taskSuccess=" + taskSuccess +
                ", executor='" + executor + '\'' +
                ", taskPrecedence='" + taskPrecedence + '\'' +
                ", taskName='" + taskName + '\'' +
                '}';
    }
}
