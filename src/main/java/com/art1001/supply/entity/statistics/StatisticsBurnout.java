package com.art1001.supply.entity.statistics;

import java.util.Arrays;

/**
 * @Auther: yanglujing
 * @Date: 2019/4/18 16:40
 * @Description: 任务燃尽图返回实体类
 */
public class StatisticsBurnout {

    /*
     * 日期数组
     */
    private  String[]  everyDate;

    /*
     * 实际任务数组
     */
        private  Integer[]  trueTask;

    /*
     * 理想任务数组
     */
    private  Double[]  idealTask;

    /*
     * 累计任务
     */
    private  Integer[]  cumulativeTask;

    /*
     * 累计完成任务
     */
    private  Integer[]   completionTask;



    public String[] getEveryDate() {
        return everyDate;
    }

    public void setEveryDate(String[] everyDate) {
        this.everyDate = everyDate;
    }

    public Integer[] getTrueTask() {
        return trueTask;
    }

    public void setTrueTask(Integer[] trueTask) { this.trueTask = trueTask; }

    public Double[] getIdealTask() {
        return idealTask;
    }

    public void setIdealTask(Double[] idealTask) {
        this.idealTask = idealTask;
    }

    public Integer[] getCumulativeTask() { return cumulativeTask; }

    public void setCumulativeTask(Integer[] cumulativeTask) { this.cumulativeTask = cumulativeTask; }

    public Integer[] getCompletionTask() { return completionTask; }

    public void setCompletionTask(Integer[] completionTask) { this.completionTask = completionTask; }

    @Override
    public String toString() {
        return "StatisticsBurnout{" +
                "everyDate=" + Arrays.toString(everyDate) +
                ", trueTask=" + Arrays.toString(trueTask) +
                ", idealTask=" + Arrays.toString(idealTask) +
                '}';
    }
}
