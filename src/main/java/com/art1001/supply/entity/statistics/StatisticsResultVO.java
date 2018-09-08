package com.art1001.supply.entity.statistics;



/**
 * 统计页面的总量概览详细数据实体类
 *
 * @author lujing
 * @date 2018-09-05 10:04
 */
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




    //封装
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTaskCase() {
        return taskCase;
    }

    public void setTaskCase(String taskCase) {
        this.taskCase = taskCase;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public String getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
    }

    public String getListView() {
        return listView;
    }

    public void setListView(String listView) {
        this.listView = listView;
    }

    public int getOverdueNum() {
        return overdueNum;
    }

    public void setOverdueNum(int overdueNum) {
        this.overdueNum = overdueNum;
    }

    public int getDynamicNum() {
        return dynamicNum;
    }

    public void setDynamicNum(int dynamicNum) {
        this.dynamicNum = dynamicNum;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getTaskCountString() {
        return taskCountString;
    }

    public void setTaskCountString(String taskCountString) {
        this.taskCountString = taskCountString;
    }

    public int getTaskCountInt() {
        return taskCountInt;
    }

    public void setTaskCountInt(int taskCountInt) {
        this.taskCountInt = taskCountInt;
    }

    public String getTaskDayNum() {
        return taskDayNum;
    }

    public void setTaskDayNum(String taskDayNum) {
        this.taskDayNum = taskDayNum;
    }

    public String getTaskDayGross() {
        return taskDayGross;
    }

    public void setTaskDayGross(String taskDayGross) {
        this.taskDayGross = taskDayGross;
    }

    public int getUnfinishTaskNum() {
        return unfinishTaskNum;
    }

    public void setUnfinishTaskNum(int unfinishTaskNum) {
        this.unfinishTaskNum = unfinishTaskNum;
    }

    public int getFinishTaskNum() {
        return finishTaskNum;
    }

    public void setFinishTaskNum(int finishTaskNum) {
        this.finishTaskNum = finishTaskNum;
    }

    public String getTaskPrecedence() {
        return taskPrecedence;
    }

    public void setTaskPrecedence(String taskPrecedence) {
        this.taskPrecedence = taskPrecedence;
    }

    @Override
    public String toString() {
        return "StatisticsResultVO{" +
                "createTime='" + createTime + '\'' +
                ", finishTime='" + finishTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", taskCase='" + taskCase + '\'' +
                ", taskName='" + taskName + '\'' +
                ", executor='" + executor + '\'' +
                ", taskGroup='" + taskGroup + '\'' +
                ", listView='" + listView + '\'' +
                ", overdueNum=" + overdueNum +
                ", dynamicNum=" + dynamicNum +
                ", changeType='" + changeType + '\'' +
                ", taskCountString=" + taskCountString +
                ", taskCountInt=" + taskCountInt +
                ", taskDayNum='" + taskDayNum + '\'' +
                ", taskDayGross='" + taskDayGross + '\'' +
                ", unfinishTaskNum=" + unfinishTaskNum +
                ", finishTaskNum=" + finishTaskNum +
                ", taskPrecedence='" + taskPrecedence + '\'' +
                '}';
    }
}
