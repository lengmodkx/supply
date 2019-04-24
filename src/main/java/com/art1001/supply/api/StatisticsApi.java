package com.art1001.supply.api;

import com.art1001.supply.entity.statistics.*;
import com.art1001.supply.service.statistics.StatisticsService;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
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
     * @return Statistics 对象
     */
    @GetMapping(value = "getPieChart/{projectId}")
    public Statistics projectStatistics(@PathVariable("projectId") String projectId){

        try {

            Statistics statistics=new Statistics();
            StaticDto sto = new StaticDto();


            //获取总任务数
            Integer count=this.statisticsService.getCountTask(projectId,new StaticDto());


            statistics.setCount(count);


            //根据项目id获取饼图数据
            List<StatisticsPie> staticPies= this.statisticsService.getPieChart(projectId,count,sto);
            //根据项目id获取折线图数据
            StatisticsHistogram staticHistograms=this.statisticsService.getHistogramsChart(projectId,sto);
            //根据项目id获取燃尽图数据   type=1 获取燃尽图数据  type=0 包含累计图数据
            StatisticsBurnout statisticsBurnout=this.statisticsService.getTaskBurnout(projectId,0, sto);
            //根据项目id获取累计数据
            //StatisticsBurnout statisticsAdd=this.statisticsService.selectProjectProgress(projectId);

            statistics.setPieData(staticPies);
            statistics.setStaticHistogram(staticHistograms);
            statistics.setStatisticsBurnout(statisticsBurnout);
            //statistics.setStatisticsAdd(statisticsAdd);
            return statistics;

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return  null;
    }



    /**
     * 饼图统计数据集
     * @param projectId 项目id
     * @return Statistics 对象
     */
    @GetMapping(value = "getPieSource/{projectId}/{data}")
    public Statistics pieStatistics(@PathVariable("projectId") String projectId ,@PathVariable("data")String dto){

        StaticDto sto=null;
        if(dto!=null){
            JSONObject jsonObject=JSONObject.fromObject(dto);
            sto=(StaticDto)JSONObject.toBean(jsonObject, StaticDto.class);
        }

        try {

            Statistics statistics=this.getCondition(projectId);
            //获取总任务数
            Integer count=this.statisticsService.getCountTask(projectId,sto);
            //根据项目id获取饼图数据
            List<StatisticsPie> staticPies= this.statisticsService.selectExcutorTask(projectId,count,sto);

            statistics.setPieData(staticPies);

            TitleVO title1=new TitleVO("执行者","name");
            TitleVO title2=new TitleVO("任务数","y");




            ArrayList<TitleVO> arrayList=new ArrayList<>();
            arrayList.add(title1);
            arrayList.add(title2);

            statistics.setTitleList(arrayList);

            return statistics;

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return  null;
    }


    /**
     * 柱状图统计数据集
     * @param projectId 项目id
     * @return Statistics 对象
     */
    @GetMapping(value = "getHistogramSource/{projectId}/{data}")
    public Statistics histogramStatistics(@PathVariable("projectId") String projectId,@PathVariable("data")String dto){

        StaticDto sto=null;
        if(dto!=null){
            JSONObject jsonObject=JSONObject.fromObject(dto);
            sto=(StaticDto)JSONObject.toBean(jsonObject, StaticDto.class);
        }

        try {
            Statistics statistics=this.getCondition(projectId);
            //根据项目id获取折线图数据
            StatisticsHistogram staticHistograms=this.statisticsService.getHistogramsChart(projectId,sto);
            statistics.setStaticHistogram(staticHistograms);


            ArrayList<TitleVO> arrayList=new ArrayList<>();
            TitleVO title=new TitleVO("执行者","name");
            arrayList.add(title);
            title=new TitleVO("任务数","y");
            arrayList.add(title);


            statistics.setTitleList(arrayList);

            return statistics;

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return  null;
    }


    /**
     * 任务燃尽图统计数据集
     * @param projectId 项目id
     * @return Statistics 对象
     */
    @GetMapping(value = "getBurnoutSource/{projectId}/{data}")
    public Statistics burnoutStatistics(@PathVariable("projectId") String projectId,@PathVariable("data")String dto){

        StaticDto sto=null;
        if(dto!=null){
            JSONObject jsonObject=JSONObject.fromObject(dto);
            sto=(StaticDto)JSONObject.toBean(jsonObject, StaticDto.class);
        }


        try {
            Statistics statistics=this.getCondition(projectId);
            //根据项目id获取燃尽图数据
            StatisticsBurnout statisticsBurnout=this.statisticsService.getTaskBurnout(projectId,1,sto);
            statistics.setStatisticsBurnout(statisticsBurnout);



            ArrayList<TitleVO> arrayList=new ArrayList<>();
            TitleVO title=new TitleVO("时间","createTime");
            arrayList.add(title);
            title=new TitleVO("任务","taskName");
            arrayList.add(title);
            title=new TitleVO("执行者","executor");
            arrayList.add(title);
            title=new TitleVO("变动类型","changeType");
            arrayList.add(title);
            title=new TitleVO("任务数","taskCountString");
            arrayList.add(title);

            statistics.setTitleList(arrayList);
            return statistics;

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return  null;
    }





    /**
     * 任累计图统计数据集
     * @param projectId 项目id
     * @return Statistics 对象
     */
    @GetMapping(value = "getAddSource/{projectId}/{data}")
    public Statistics addStatistics(@PathVariable("projectId") String projectId,@PathVariable("data")String dto){

        StaticDto sto=null;
        if(dto!=null){
            JSONObject jsonObject=JSONObject.fromObject(dto);
            sto=(StaticDto)JSONObject.toBean(jsonObject, StaticDto.class);
        }

        try {
            Statistics statistics=this.getCondition(projectId);
            //根据项目id获取燃尽图数据
            StatisticsBurnout statisticsBurnout=this.statisticsService.getTaskBurnout(projectId,2, sto);
            statistics.setStatisticsBurnout(statisticsBurnout);


            ArrayList<TitleVO> arrayList=new ArrayList<>();
            TitleVO title1=new TitleVO("创建时间","createTime");
            arrayList.add(title1);
            title1=new TitleVO("任务","taskName");
            arrayList.add(title1);
            title1=new TitleVO("执行者","executor");
            arrayList.add(title1);



            statistics.setTitleList(arrayList);


            return statistics;

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return  null;
    }

    //获取分组数据
    private  Statistics getCondition( String projectId){
        return  this.statisticsService.getGroupData(projectId);
    }
}
