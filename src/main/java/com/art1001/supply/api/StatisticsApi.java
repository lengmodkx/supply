package com.art1001.supply.api;

import com.art1001.supply.service.statistics.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    private StatisticsService statisticsService;


    /**
     * 页面项目统计总览
     * @param projectId 项目id
     * @return
     */
    @GetMapping(value = "getPieChart")
    public  String  ProjectStatistics(String projectId){

          //根据项目id获取饼图数据
          String result=statisticsService.getPieChart(projectId);

        return result;
    }



}
