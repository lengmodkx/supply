package com.art1001.supply.entity.statistics;

/**
 * @Auther: yangluljing
 * @Date: 2019/4/15 11:45
 * @Description: 饼形统计图数据
 */
public class StatisticsPie {

    private String name;
    private Integer y;

    public StatisticsPie() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "StatisticsPie{" +
                "name='" + name + '\'' +
                ", y=" + y +
                '}';
    }
}
