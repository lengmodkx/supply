package com.art1001.supply.entity.statistics;



/**
 * 统计页面的总量概览详细数据实体类
 *
 * @author lujing
 * @date 2018-09-05 10:04
 */
public class TotalOverView {

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
     *创建时间
     */
    private String createTime;

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
    private int dymamicNum;

    /**
     *变动类型
     */
    private String change;

    /**
     *任务数
     */
    private String taskNum;

    /**
     *完成天数
     */
    private String taskDayNum;


    //封装
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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

    public int getDymamicNum() {
        return dymamicNum;
    }

    public void setDymamicNum(int dymamicNum) {
        this.dymamicNum = dymamicNum;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(String taskNum) {
        this.taskNum = taskNum;
    }

    public String getTaskCase() {
        return taskCase;
    }

    public void setTaskCase(String taskCase) {
        this.taskCase = taskCase;
    }

    public String getTaskDayNum() {
        return taskDayNum;
    }

    public void setTaskDayNum(String taskDayNum) {
        this.taskDayNum = taskDayNum;
    }

    @Override
    public String toString() {
        return "TotalOverView{" +
                "finishTime='" + finishTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", taskCase='" + taskCase + '\'' +
                ", createTime='" + createTime + '\'' +
                ", taskName='" + taskName + '\'' +
                ", executor='" + executor + '\'' +
                ", taskGroup='" + taskGroup + '\'' +
                ", listView='" + listView + '\'' +
                ", overdueNum=" + overdueNum +
                ", dymamicNum=" + dymamicNum +
                ", change='" + change + '\'' +
                ", taskNum='" + taskNum + '\'' +
                ", taskDayNum='" + taskDayNum + '\'' +
                '}';
    }
}
