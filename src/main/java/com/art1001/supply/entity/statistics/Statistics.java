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
     * 分组名
     */
    private String name;

    /**
     * 任务达成该分组的要求的 数量
     */
    private int count;

    /**
     * 该任务完成度的百分比
     */
    private double percentage;

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
