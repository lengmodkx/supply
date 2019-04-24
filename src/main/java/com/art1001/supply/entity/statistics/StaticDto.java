package com.art1001.supply.entity.statistics;

/**
 * @Auther: Administrator
 * @Date: 2019/4/23 15:32
 * @Description:
 */
public class StaticDto {

    /**
     *根据成员查询
     */
    private String taskMember;

    /**
     *根据任务分组
     */
    private String taskGroup;

    /**
     *根据是否成功
     */
        private String taskCase;

    /**
     *根据天数，开始时天数和结束时天数
     */
    private String startDay;

    private String endDay;

    /**
     *根据天数
     */
    private  int  DayNum;

    /**
     *根据天数
     */
    private  String  taskDay;



    public String getTaskDay() {
        return taskDay;
    }

    public void setTaskDay(String taskDay) {
        this.taskDay = taskDay;
    }

    public int getDayNum() {
        return DayNum;
    }

    public void setDayNum(int dayNum) {
        DayNum = dayNum;
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

    @Override
    public String toString() {
        return "StaticDto{" +
                "taskMember='" + taskMember + '\'' +
                ", taskGroup='" + taskGroup + '\'' +
                ", taskCase='" + taskCase + '\'' +
                '}';
    }
}
