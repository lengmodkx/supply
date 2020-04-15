package com.art1001.supply.api;

import com.art1001.supply.entity.statistics.*;
import com.art1001.supply.service.statistics.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.*;

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
     *
     * @param projectId 项目id
     * @return Statistics 对象
     */
    @GetMapping(value = "getPieChart/{projectId}")
    public Statistics projectStatistics(@PathVariable("projectId") String projectId) {

        try {

            Statistics statistics = new Statistics();
            StaticDto sto = new StaticDto();


            //获取总任务数
            Integer count = this.statisticsService.getCountTask(projectId, sto);


            //statistics.setCount(count);


            //根据项目id获取饼图数据
            List<StatisticsPie> staticPies = this.statisticsService.getPieChart(projectId, count, sto);
            //根据项目id获取柱状图数据
            StatisticsHistogram staticHistograms = this.statisticsService.getHistogramsChart(projectId, sto);
            //根据项目id获取燃尽图数据   type=1 获取燃尽图数据  type=0 包含累计图数据
            StatisticsBurnout statisticsBurnout = this.statisticsService.getTaskBurnout(projectId, 0, sto);
            //根据项目id获取累计数据
            //StatisticsBurnout statisticsAdd=this.statisticsService.selectProjectProgress(projectId);
            //查询出该项目下的所有任务 状态数量概览
            List<QueryVO> countList = this.statisticsService.findTaskCountOverView(projectId, sto);

            statistics.setCountData(countList);
            statistics.setPieData(staticPies);
            statistics.setStaticHistogram(staticHistograms);
            statistics.setStatisticsBurnout(statisticsBurnout);
            //statistics.setStatisticsAdd(statisticsAdd);
            return statistics;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 饼图统计数据集
     *
     * @param projectId 项目id
     * @return Statistics 对象
     */
    @GetMapping(value = "getPieSource/{projectId}")
    public Statistics pieStatistics(@PathVariable("projectId") String projectId, @RequestParam("data") String dto) {

        StaticDto sto = this.getStaticDto(dto);

        try {

            Statistics statistics = this.getCondition(projectId);
            //按任务数查询
            if (sto.getTaskCount()== null || "按任务数".equals(sto.getTaskCount())) {

                    //获取总任务数
                    Integer count = this.statisticsService.getCountTask(projectId, sto);
                    //根据项目id获取饼图数据
                    List<StatisticsPie> staticPies = this.statisticsService.selectExcutorTask(projectId, count, sto);

                    statistics.setPieData(staticPies);

                    TitleVO title1 = new TitleVO("执行者", "name");
                    TitleVO title2 = new TitleVO("任务数", "y");

                    ArrayList<TitleVO> arrayList = new ArrayList<>();
                    arrayList.add(title1);
                    arrayList.add(title2);

                    statistics.setTitleList(arrayList);

                    return statistics;
                } else {
                    //按计划工时查询

                    //获取总计划工时
                    Double sum = this.statisticsService.getTaskManHour(projectId, sto);

                    //根据项目id获取饼图数据
                    List<StatisticsPie> staticPies = this.statisticsService.selectEveryExcutorTask(projectId, sum, sto);
                    statistics.setPieData(staticPies);

                    TitleVO title1 = new TitleVO("执行者", "name");
                    TitleVO title2 = new TitleVO("计划工时（单位：h）", "y");

                    ArrayList<TitleVO> arrayList = new ArrayList<>();
                    arrayList.add(title1);
                    arrayList.add(title2);

                    statistics.setTitleList(arrayList);

                    return statistics;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 柱状图统计数据集
     *
     * @param projectId 项目id
     * @return Statistics 对象
     */
    @GetMapping(value = "getHistogramSource/{projectId}")
    public Statistics histogramStatistics(@PathVariable("projectId") String projectId, @RequestParam("data") String dto) {

        StaticDto sto = this.getStaticDto(dto);

        try {
            Statistics statistics=this.getCondition(projectId);
            List<StatisticsHistogram> staticHistograms=this.statisticsService.getHistogramsData(projectId,sto);
            statistics.setHisResultlist(staticHistograms);
            if (!staticHistograms.isEmpty()) {
                String[] nameArray = new String[staticHistograms.size()];
                Integer[] dataArray = new Integer[staticHistograms.size()];
                Double[]  doubleArray = new Double[staticHistograms.size()];

                for (int i = 0; i < staticHistograms.size(); i++) {
                    nameArray[i] = staticHistograms.get(i).getName();
                    if (staticHistograms.get(i).getData() != null ){
                        dataArray[i] = staticHistograms.get(i).getData();
                    }else{
                        doubleArray[i] = staticHistograms.get(i).getDoubleData();
                    }
                }
                StatisticsHistogram staticHistogram = new StatisticsHistogram();
                staticHistogram.setNameArray(nameArray);
                staticHistogram.setDoubleArray(doubleArray);
                staticHistogram.setDataArray(dataArray);
                statistics.setStaticHistogram(staticHistogram);
                if (null != sto.getTaskCount()){
                    staticHistogram.setTaskType(sto.getTaskCount());
                }
            } else {
                StatisticsHistogram statisticsHistogram = new StatisticsHistogram();
                if (null != sto.getTaskCount()){
                    statisticsHistogram.setTaskType(sto.getTaskCount());
                }
                statistics.setStaticHistogram(statisticsHistogram);

            }
            ArrayList<TitleVO> arrayList = new ArrayList<>();
            TitleVO title = new TitleVO("执行者", "name");
            arrayList.add(title);
            //根据项目id获取柱状图数据
            if (sto.getTaskCount() == null || "按任务数".equals(sto.getTaskCount())){
                title = new TitleVO("任务数", "data");
            }else {
                title = new TitleVO("计划工时（单位：h）", "doubleData");
            }
            arrayList.add(title);
            statistics.setTitleList(arrayList);
            return statistics;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 任务燃尽图统计数据集
     *
     * @param projectId 项目id
     * @return Statistics 对象
     */
    @GetMapping(value = "getBurnoutSource/{projectId}")
    public Statistics burnoutStatistics(@PathVariable("projectId") String projectId, @RequestParam("data") String dto) {

        StaticDto sto = this.getStaticDto(dto);
        if (dto != null) {
            JSONObject jsonObject = JSONObject.fromObject(dto);
            sto = (StaticDto) JSONObject.toBean(jsonObject, StaticDto.class);
        }

        try {
            Statistics statistics = this.getCondition(projectId);
            //根据项目id获取燃尽图数据
            StatisticsBurnout statisticsBurnout = this.statisticsService.getTaskBurnout(projectId, 1, sto);
            statistics.setStatisticsBurnout(statisticsBurnout);

            ArrayList<TitleVO> arrayList = new ArrayList<>();
            TitleVO title = new TitleVO("时间", "createTime");
            arrayList.add(title);
            title = new TitleVO("任务", "taskName");
            arrayList.add(title);
            title = new TitleVO("执行者", "executor");
            arrayList.add(title);
            title = new TitleVO("变动类型", "changeType");
            arrayList.add(title);
            title = new TitleVO("任务数", "taskCountString");
            arrayList.add(title);

            statistics.setTitleList(arrayList);
            return statistics;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 任务累计图统计数据集
     *
     * @param projectId 项目id
     * @return Statistics 对象
     */
    @GetMapping(value = "getAddSource/{projectId}")
    public Statistics addStatistics(@PathVariable("projectId") String projectId, @RequestParam("data") String dto) {

        StaticDto sto = this.getStaticDto(dto);
        if (dto != null) {
            JSONObject jsonObject = JSONObject.fromObject(dto);
            sto = (StaticDto) JSONObject.toBean(jsonObject, StaticDto.class);
        }

        try {
            Statistics statistics = this.getCondition(projectId);
            //根据项目id获取燃尽图数据
            StatisticsBurnout statisticsBurnout = this.statisticsService.getTaskBurnout(projectId, 2, sto);
            statistics.setStatisticsBurnout(statisticsBurnout);


            ArrayList<TitleVO> arrayList = new ArrayList<>();
            TitleVO title1 = new TitleVO("创建时间", "createTime");
            arrayList.add(title1);
            title1 = new TitleVO("任务", "taskName");
            arrayList.add(title1);
            title1 = new TitleVO("执行者", "executor");
            arrayList.add(title1);

            statistics.setTitleList(arrayList);

            return statistics;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 概览表详细数据集
     *
     * @param projectId 项目id
     * @return Statistics 对象
     */
    @GetMapping(value = "getCountData/{projectId}")
    public Statistics getCountData(@PathVariable("projectId") String projectId, @RequestParam("data") String dto) {
        StaticDto sto = this.getStaticDto(dto);
        if (dto != null) {
            JSONObject jsonObject = JSONObject.fromObject(dto);
            sto = (StaticDto) JSONObject.toBean(jsonObject, StaticDto.class);
            if (!sto.getTaskCase().isEmpty()) {
                if ("已完成".equals(sto.getTaskCase())) {
                    sto.setTaskCondition(1);
                } else {
                    sto.setTaskCondition(0);
                }
            }
        }

        List<QueryVO> taskCountOverView = this.statisticsService.findTaskCountOverView(projectId, sto);
        Statistics statistics = getCountTable(projectId, "已完成", dto);
        statistics.setCountData(taskCountOverView);
        return statistics;
    }


    /**
     * 查询概览图表数据
     *
     * @param projectId 项目id
     * @return Statistics 对象
     */
    @GetMapping(value = "getCountTable/{projectId}/{divName}")
    public Statistics getCountTable(@PathVariable("projectId") String projectId, @PathVariable("divName") String divName, @RequestParam("data") String dto) {

        StaticDto sto = this.getStaticDto(dto);


        Statistics condition = this.statisticsService.getCountTable(divName, projectId, sto);

        Statistics condition1 = getCondition(projectId);
        condition.setExecutor(condition1.getExecutor());
        condition.setTaskGroup(condition1.getTaskGroup());
        return condition;
    }

    //获取分组数据
    private Statistics getCondition(String projectId) {
        return this.statisticsService.getGroupData(projectId);
    }


    private StaticDto getStaticDto(String dto) {
        JSONObject jsonObject = JSONObject.fromObject(dto);
        StaticDto sto = (StaticDto) JSONObject.toBean(jsonObject, StaticDto.class);
        return sto;
    }

}
