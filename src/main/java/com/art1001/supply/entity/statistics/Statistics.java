package com.art1001.supply.entity.statistics;

import lombok.Data;

import java.util.List;

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
    List<StatisticsPie> pieData;

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
      * 执行者分组
      */
      List<QueryVO> executor;

    /**
     * 任务分组
     */
      List<QueryVO> taskGroup;

    /*
     * 需要返回的table表头
     */
    private List<TitleVO> titleList ;

    /*
     * 总量概览
     */
    List<QueryVO>   countData;

    /**
     * 柱状图表数据
     */
    List<StatisticsHistogram>   hisResultlist;

    /*
     *  概览数据
     */
    List<StatisticsResultVO> sticsResultList;


    public List<StatisticsHistogram> getHisResultlist() {
        return hisResultlist;
    }

    public void setHisResultlist(List<StatisticsHistogram> hisResultlist) {
        this.hisResultlist = hisResultlist;
    }

    public List<QueryVO> getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(List<QueryVO> taskGroup) {
        this.taskGroup = taskGroup;
    }

    public List<QueryVO> getCountData() {
        return countData;
    }

    public void setCountData(List<QueryVO> countData) {
        this.countData = countData;
    }

    public List<StatisticsResultVO> getSticsResultList() {
        return sticsResultList;
    }

    public void setSticsResultList(List<StatisticsResultVO> sticsResultList) {
        this.sticsResultList = sticsResultList;
    }

    public List<QueryVO> getExecutor() {
        return executor;
    }

    public void setExecutor(List<QueryVO> executor) {
        this.executor = executor;
    }

    public List<TitleVO> getTitleList() {
        return titleList;
    }

    public void setTitleList(List<TitleVO> titleList) {
        this.titleList = titleList;
    }

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


    public List<StatisticsPie> getPieData() {
        return pieData;
    }

    public void setPieData(List<StatisticsPie> pieData) {
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
