package com.art1001.supply.service.statistics;

import com.art1001.supply.entity.statistics.StatisticsBurnout;
import com.art1001.supply.entity.statistics.StatisticsHistogram;
import com.art1001.supply.entity.statistics.StatisticsPie;

import java.util.List;

/**
 * @author yanglujing
 * @Title: StatisticsService
 * @Description: TODO 分享api
 * @date 2019/4/13 16:47
 */
public interface StatisticsService {


    //获取总任务数
    Integer getCountTask(String projectId);

    //获取统计页面饼图数据
    List<StatisticsPie> getPieChart(String projectId, Integer count);

    //获取统计页面柱状图数据
    StatisticsHistogram getHistogramsChart(String projectId);

    //获取统计页面任务燃尽图数据
    StatisticsBurnout getTaskBurnout(String projectId);

    //获取统计页面累计任务数据
    StatisticsBurnout selectProjectProgress(String projectId);


}
