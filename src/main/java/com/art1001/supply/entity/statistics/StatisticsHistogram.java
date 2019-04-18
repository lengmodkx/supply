package com.art1001.supply.entity.statistics;

/**
 * @Auther: yanglujing
 * @Date: 2019/4/17 18:39
 * @Description:  柱状图数据
 */
public class StatisticsHistogram {

    private String name = "待认领";

    private Integer data;

    private String[] nameArray ;

    private Integer[] dataArray ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getNameArray() { return nameArray; }

    public void setNameArray(String[] nameArray) { this.nameArray = nameArray; }

    public Integer[] getDataArray() { return dataArray; }

    public void setDataArray(Integer[] dataArray) {
        this.dataArray = dataArray;
    }

    public Integer getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "StatisticsHistogram{" +
                "name='" + name + '\'' +
                ", data=" + data +
                '}';
    }
}
