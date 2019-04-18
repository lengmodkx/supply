package com.art1001.supply.service.statistics.impl;

import com.art1001.supply.entity.statistics.*;
import com.art1001.supply.mapper.statistics.StaticInfoMapper;
import com.art1001.supply.service.statistics.StatisticsService;
import com.art1001.supply.util.CalculateProportionUtil;
import com.art1001.supply.util.DateUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    private StaticInfoMapper statisticsMapper;

    /**
     * 获取任务总数
     */
    @Override
    public Integer getCountTask(String projectId) {
        return statisticsMapper.getCountTask(projectId);
    }

    /**
     * 获取饼图数据
     */
    @Override
    public List<StatisticsPie> getPieChart(String projectId,Integer count) {

        //获取每个用户的任务数
        List<StatisticsPie> statisticsPies=statisticsMapper.getPieDate(projectId);

            for (int i=0;i<statisticsPies.size();i++) {
                String num = CalculateProportionUtil.proportionDouble( Float.valueOf(statisticsPies.get(i).getY().toString()),(float)count, 2);
                statisticsPies.get(i).setY(Float.valueOf(num));
        }

        return  statisticsPies;
    }

    /**
     * 获取柱状图数据
     */
    @Override
    public StatisticsHistogram getHistogramsChart(String projectId) {

        //系统当前时间
        Long currentDate = System.currentTimeMillis() / 1000;

        //获取每个用户的数据
        List<StatisticsHistogram> statisticsHistograms=statisticsMapper.getHistogramsDate(projectId,currentDate);

        String[] nameArray = new String[statisticsHistograms.size()];
        Integer[] dataArray = new Integer[statisticsHistograms.size()];

        for(int i=0;i<statisticsHistograms.size();i++){
              nameArray[i] = statisticsHistograms.get(i).getName();
              dataArray[i] = statisticsHistograms.get(i).getData();
        }

       StatisticsHistogram staticHistogram = new StatisticsHistogram();
        staticHistogram.setNameArray(nameArray);
        staticHistogram.setDataArray(dataArray);

        return staticHistogram;
    }

    //获取任务燃尽图数据
    public  StatisticsBurnout getTaskBurnout(String projectId) {

        Long currentDate = System.currentTimeMillis() / 1000;

        //过去七天
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, - 7);
        Date d = c.getTime();
        String sevenDayAgo = format.format(d);

        try {
            //获取七天的总数据
            //List<StatisticsResultVO> statisticsResultVOList = this.statisticsMapper.selectTaskBurnOut(projectId);
            //先获取七天前的任务总量
            Integer count = this.statisticsMapper.taskSevenDayAgo(projectId, currentDate, 7);
            //项目进展走势
            List<StatisticsResultVO> taskOfProgress = this.statisticsMapper.taskOfProgress(projectId, currentDate);
            //计算累计任务量
            Map<String, Integer> taskCountMap = DateUtil.dateComplement(taskOfProgress, count, 7);
            Map<String, Double> stringMap = DateUtil.taskBurnout(taskCountMap, 7);

            List<StatisticsResultVO> taskOfFinishProgress = this.statisticsMapper.taskOfFinishProgress(projectId, currentDate);
            //跟据上面查询的taskOfFinishedProgress列表计算每日完成任务量
            Map<String, Integer> taskEveryDayMap = DateUtil.everyDayDateComplement(taskOfFinishProgress, 7);


            //项目进展走势
            Integer[] firstArray = new Integer[taskCountMap.size()];
            int i = 0;
            if (taskCountMap != null && taskCountMap.size() > 0) {
                for (Map.Entry<String, Integer> entry : taskCountMap.entrySet()) {
                    firstArray[i] = entry.getValue();
                    i++;
                }
            }
            //获取七天前的完成任务量
            int finishCount = this.statisticsMapper.taskFinishOfSevenDayAgo(projectId, currentDate);
            Map<String, Integer> finishMap = DateUtil.dateComplement(taskOfFinishProgress, finishCount, 7);
            Integer[] secondArray = new Integer[finishMap.size()];
            i = 0;
            for (Map.Entry<String, Integer> entry : finishMap.entrySet()) {
                secondArray[i] = entry.getValue();
                i++;
            }

            i = 0;
            Double[] everyDateInt = new Double[stringMap.size()];
            String[] everyDateName = new String[stringMap.size()];
            if (stringMap != null && stringMap.size() > 0) {
                for (Map.Entry<String, Double> entry : stringMap.entrySet()) {
                    everyDateInt[i] = entry.getValue();
                    everyDateName[i] = entry.getKey();
                    i++;
                }
            }
            i = 0;
            Integer[] secondInt = new Integer[taskEveryDayMap.size()];
            if (taskEveryDayMap != null && taskEveryDayMap.size() > 0) {
                for (Map.Entry<String, Integer> entry : taskEveryDayMap.entrySet()) {
                    if (taskCountMap != null && taskCountMap.size() > 0) {
                        for (Map.Entry<String, Integer> everyEntry : taskCountMap.entrySet()) {
                            if (everyEntry.getKey().equals(entry.getKey())) {
                                secondInt[i] = everyEntry.getValue() - entry.getValue();
                                i++;
                            }
                        }
                    }
                }
            }
            // 需要返回的chart数据
            StatisticsBurnout statisticsBurnout = new StatisticsBurnout();
            statisticsBurnout.setEveryDate(everyDateName);
            statisticsBurnout.setTrueTask(secondInt);
            statisticsBurnout.setIdealTask(everyDateInt);
            statisticsBurnout.setCumulativeTask(firstArray);
            statisticsBurnout.setCompletionTask(secondArray);

         return  statisticsBurnout;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 项目进展走势
     */
    public StatisticsBurnout selectProjectProgress(String projectId) {
        return null;
    }



}
