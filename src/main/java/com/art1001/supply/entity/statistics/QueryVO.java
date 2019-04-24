package com.art1001.supply.entity.statistics;

/**
 * @Auther: yanglujing
 * @Date: 2019/4/22 17:01
 * @Description: 统计页面查询条件
 */
public class QueryVO {

    private String value;

    private String label;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "StatisticsQuery{" +
                "value='" + value + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
