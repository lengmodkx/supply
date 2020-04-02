package com.art1001.supply.entity.statistics;

import lombok.Data;

import java.util.Arrays;

/**
 * @Auther: yanglujing
 * @Date: 2019/4/17 18:39
 * @Description:  柱状图数据
 */
@Data
public class StatisticsHistogram {

    private String  taskType;

    private String name = "待认领";

    private Integer data;

    private Double doubleData;

    private String[] nameArray ;

    private Integer[] dataArray ;

    private Double[] doubleArray ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name==null){
            this.name="待认领";
        }else {
            this.name = name;
        }

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
                ", nameArray=" + Arrays.toString(nameArray) +
                ", dataArray=" + Arrays.toString(dataArray) +
                '}';
    }
}
