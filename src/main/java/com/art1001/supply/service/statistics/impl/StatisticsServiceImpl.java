package com.art1001.supply.service.statistics.impl;

import com.art1001.supply.entity.statistics.StatisticsPie;
import com.art1001.supply.mapper.statistics.StatisticsMapper;
import com.art1001.supply.service.statistics.StatisticsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    private StatisticsMapper statisticsMapper;

    @Override
    public List<StatisticsPie> getPieChart(String projectId) {
        //获取任务总数
        Integer count=statisticsMapper.getCountTask(projectId);
        //获取每个用户的任务数
        List<StatisticsPie> statisticsPies=statisticsMapper.getPieDate(projectId);

        //StatisticsPie[]  pieDate=new StatisticsPie[statisticsPies.size()];

       /* StringBuilder result=new StringBuilder();
        StringBuilder result2=new StringBuilder();
        result.append("[");*/

       /*for (int i=0; i<statisticsPies.size() ;i++){
            if(statisticsPies.get(i).getName()!=null){
                result.append("{name:").append(statisticsPies.get(i).getName()).append(",").append("y:").append(statisticsPies.get(i).getY()).append("}").append(",");
            }else{
                result.append("{name:").append("待认领").append(",").append("y:").append(statisticsPies.get(i).getY()).append("}").append(",");
            }
        }*/
        /*result2.append(result.substring(0, result.length()-1));
        result2.append("]");*/
        //return result2.toString();
        return  statisticsPies;
    }


}
