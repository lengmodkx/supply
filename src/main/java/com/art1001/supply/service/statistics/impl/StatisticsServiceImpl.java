package com.art1001.supply.service.statistics.impl;

import com.art1001.supply.entity.statistics.Statistics;
import com.art1001.supply.entity.statistics.StatisticsPie;
import com.art1001.supply.mapper.statistics.StatisticsMapper;
import com.art1001.supply.service.statistics.StatisticsService;
import com.art1001.supply.util.CalculateProportionUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    private StatisticsMapper statisticsMapper;

    @Override
    public List<StatisticsPie> getPieChart(String projectId) {

        Statistics statistics=new Statistics();
        //获取任务总数
        Integer count=statisticsMapper.getCountTask(projectId);
        //获取每个用户的任务数
        List<StatisticsPie> statisticsPies=statisticsMapper.getPieDate(projectId);

        for (int i=0;i<statisticsPies.size();i++) {
            String num = CalculateProportionUtil.proportionDouble( Float.valueOf(statisticsPies.get(i).getY().toString()),(float)count, 2);
            statisticsPies.get(i).setY(Float.valueOf(num));
            if (statisticsPies.get(i).getName()==null){
                statisticsPies.get(i).setName("待认领");
            }
        }

        Gson gson = new GsonBuilder().create();
        String resultJsonMapData = gson.toJson(statisticsPies);
        statistics.setPieData(resultJsonMapData);

        return  statisticsPies;
    }


}
