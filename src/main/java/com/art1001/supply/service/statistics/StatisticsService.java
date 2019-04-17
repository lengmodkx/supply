package com.art1001.supply.service.statistics;

import com.art1001.supply.entity.statistics.StatisticsPie;

import java.util.List;

/**
 * @author yanglujing
 * @Title: StatisticsService
 * @Description: TODO 分享api
 * @date 2019/4/13 16:47
 */
public interface StatisticsService {

    //获取统计页面饼图数据
    List<StatisticsPie> getPieChart(String projectId);

}
