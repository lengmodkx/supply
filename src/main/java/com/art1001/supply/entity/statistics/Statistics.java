package com.art1001.supply.entity.statistics;

import lombok.Data;

/**
 * @author heshaohua
 * @Title: Statistics
 * @Description: TODO
 * @date 2018/8/13 11:37
 **/
@Data
public class Statistics {

    /**
    *  饼图数据
    **/
        String pieData;

    /**
     * 柱状图数据
     */
       StatisticsHistogram staticHistogram;

    /**
     * 任务燃尽图数据
     */
       StatisticsBurnout statisticsBurnout;


     /**
     * 任务累计数据
     */
       StatisticsBurnout  statisticsAdd;



     /**
     * 分组名
     */
    private String name;

    /**
     * 任务总 数量
     */
    private int count;

    /**
     * 该任务完成度的百分比
     */
    private double percentage;

    //封装

    public String getPieData() {
        return pieData;
    }

    public void setPieData(String pieData) {
        this.pieData = pieData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public StatisticsHistogram getStaticHistogram() { return staticHistogram; }

    public void setStaticHistogram(StatisticsHistogram staticHistogram) { this.staticHistogram = staticHistogram; }

    public StatisticsBurnout getStatisticsBurnout() { return statisticsBurnout; }

    public void setStatisticsBurnout(StatisticsBurnout statisticsBurnout) { this.statisticsBurnout = statisticsBurnout; }

    public StatisticsBurnout getStatisticsAdd() { return statisticsAdd; }

    public void setStatisticsAdd(StatisticsBurnout statisticsAdd) { this.statisticsAdd = statisticsAdd; }

    @Override
    public String toString() {
        return "Statistics{" +
                "pieData=" + pieData +
                ", name='" + name + '\'' +
                ", count=" + count +
                ", percentage=" + percentage +
                '}';
    }
}
