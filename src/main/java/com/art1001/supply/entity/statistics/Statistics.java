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
    *  饼图数据list
    **/
    List<StatisticsPie> statisticsPieList;
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
}
