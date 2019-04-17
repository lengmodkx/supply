package com.art1001.supply.api;

import com.art1001.supply.entity.statistics.Statistics;
import com.art1001.supply.entity.statistics.StatisticsPie;
import com.art1001.supply.service.statistics.StatisticsService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yanglujing
 * @Title: StatisticsApi
 * @Description: TODO 统计api
 * @date 2019/4/13 15:25
 * [GET]    // 查询
 **/
@Slf4j
@RestController
@RequestMapping("statistics")
public class StatisticsApi {



    @Resource
    private StatisticsService statisticsService;


    /**
     * 页面项目统计总览
     * @param projectId 项目id
     * @return
     */
    @GetMapping(value = "getPieChart/{projectId}")
    public Statistics ProjectStatistics(@PathVariable("projectId") String projectId){

        try {
            Statistics statistics=new Statistics();
            //根据项目id获取饼图数据
            List<StatisticsPie> resultPie=this.statisticsService.getPieChart(projectId);
            for(int i=0;i<resultPie.size();i++){
                if (resultPie.get(i).getName()==null){
                    resultPie.get(i).setName("待认领");
                }
            }
            Gson gson = new GsonBuilder().create();
            String resultJsonMapData = gson.toJson(resultPie);
            statistics.setPieData(resultJsonMapData);
            return statistics;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return  null;
    }



}
