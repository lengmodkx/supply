package com.art1001.supply.service.statistics.impl;

import com.art1001.supply.entity.statistics.*;
import com.art1001.supply.mapper.statistics.StaticInfoMapper;
import com.art1001.supply.service.statistics.StatisticsService;
import com.art1001.supply.util.CalculateProportionUtil;
import com.art1001.supply.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
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
    public Integer getCountTask(String projectId, StaticDto sto) {

        return statisticsMapper.getCountTask(projectId);
    }

    /**
     * 获取饼图数据
     */
    @Override
    public List<StatisticsPie> getPieChart(String projectId, Integer count, StaticDto sto) {

        //获取每个用户的任务数
        List<StatisticsPie> statisticsPies=statisticsMapper.getPieDate(projectId,resultStatic(sto));

            for (int i=0;i<statisticsPies.size();i++) {
                String num = CalculateProportionUtil.proportionDouble( Float.valueOf(statisticsPies.get(i).getY().toString()),(float)count, 2);
                statisticsPies.get(i).setY(Float.valueOf(num));
        }

        return  statisticsPies;
    }

    /**
     * 获取饼图数据
     */
    @Override
    public List<StatisticsPie> selectExcutorTask(String projectId, Integer count, StaticDto sto) {

        //获取每个用户的任务数
        List<StatisticsPie> statisticsPies=statisticsMapper.selectExcutorTask(projectId,resultStatic(sto));
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
    public StatisticsHistogram getHistogramsChart(String projectId,StaticDto sto) {

        //系统当前时间
        Long currentDate = System.currentTimeMillis() / 1000;

        //获取每个用户的数据
        List<StatisticsHistogram> statisticsHistograms=statisticsMapper.getHistogramsDate(projectId,currentDate,sto);

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
    public  StatisticsBurnout getTaskBurnout(String projectId, Integer type, StaticDto sto) {

        Long currentDate = System.currentTimeMillis() / 1000;

        // 需要返回的chart数据
        StatisticsBurnout statisticsBurnout = new StatisticsBurnout();

        sto =  resultStatic(sto);


        try {
            //获取七天的总数据
            List<StatisticsResultVO> statisticsResultVOList = this.statisticsMapper.selectTaskBurnOut(projectId,sto);



            //先获取七天前的任务总量
            Integer count = this.statisticsMapper.taskSevenDayAgo(projectId, currentDate, sto.getDayNum());
            //项目进展走势
            List<StatisticsResultVO> taskOfProgress = this.statisticsMapper.taskOfProgress(projectId, currentDate,sto);
            //计算累计任务量
            Map<String, Integer> taskCountMap = DateUtil.dateComplement(taskOfProgress, count, sto.getDayNum());
            Map<String, Double> stringMap = DateUtil.taskBurnout(taskCountMap, sto.getDayNum());

            List<StatisticsResultVO> taskOfFinishProgress = this.statisticsMapper.taskOfFinishProgress(projectId, currentDate,sto);
            //跟据上面查询的taskOfFinishedProgress列表计算每日完成任务量
            Map<String, Integer> taskEveryDayMap = DateUtil.everyDayDateComplement(taskOfFinishProgress, sto.getDayNum());
            int i = 0;
            String[] everyDateName = new String[stringMap.size()];
            // type = 0 时包含所有数据  type = 1   燃尽图数据  type = 2  累计图数据
            if (type == 0){

                //燃尽图数据
                i = 0;
                Double[] everyDateInt = new Double[stringMap.size()];
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

                statisticsBurnout.setTrueTask(secondInt);
                statisticsBurnout.setIdealTask(everyDateInt);


                //项目进展走势
                Integer[] firstArray = new Integer[taskCountMap.size()];
                i = 0;
                if (taskCountMap != null && taskCountMap.size() > 0) {
                    for (Map.Entry<String, Integer> entry : taskCountMap.entrySet()) {
                        firstArray[i] = entry.getValue();
                        i++;
                    }
                }

                //获取七天前的完成任务量
                int finishCount = this.statisticsMapper.taskFinishOfSevenDayAgo(projectId, currentDate,sto);
                Map<String, Integer> finishMap = DateUtil.dateComplement(taskOfFinishProgress, finishCount, sto.getDayNum());
                Integer[] secondArray = new Integer[finishMap.size()];
                i = 0;
                for (Map.Entry<String, Integer> entry : finishMap.entrySet()) {
                    secondArray[i] = entry.getValue();
                    i++;
                }

                statisticsBurnout.setCumulativeTask(firstArray);
                statisticsBurnout.setCompletionTask(secondArray);

            }else if(type == 1){
                //燃尽图数据
                i = 0;
                Double[] everyDateInt = new Double[stringMap.size()];
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
                statisticsBurnout.setSticResultVOS(statisticsResultVOList);
                statisticsBurnout.setTrueTask(secondInt);
                statisticsBurnout.setIdealTask(everyDateInt);

            }else if(type == 2){

                //项目进展走势
                Integer[] firstArray = new Integer[taskCountMap.size()];
                i = 0;
                if (taskCountMap != null && taskCountMap.size() > 0) {
                    for (Map.Entry<String, Integer> entry : taskCountMap.entrySet()) {
                        firstArray[i] = entry.getValue();
                        i++;
                    }
                }

                //获取七天前的完成任务量
                int finishCount = this.statisticsMapper.taskFinishOfSevenDayAgo(projectId, currentDate, sto);
                Map<String, Integer> finishMap = DateUtil.dateComplement(taskOfFinishProgress, finishCount, sto.getDayNum());
                Integer[] secondArray = new Integer[finishMap.size()];
                i = 0;
                for (Map.Entry<String, Integer> entry : finishMap.entrySet()) {
                    everyDateName[i] = entry.getKey();
                    secondArray[i] = entry.getValue();
                    i++;
                }

                List<StatisticsResultVO> sticsResultVO = this.statisticsMapper.selectProjectProgress(projectId,sto);

                statisticsBurnout.setSticResultVOS(sticsResultVO);
                statisticsBurnout.setCumulativeTask(firstArray);
                statisticsBurnout.setCompletionTask(secondArray);
            }
            statisticsBurnout.setEveryDate(everyDateName);


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

    @Override
    public Statistics getGroupData(String projectId) {

        List<QueryVO> executor = this.statisticsMapper.getExecutorGroup(projectId);
        //删除列表中的null元素
        executor.removeAll(Collections.singleton(null));
        QueryVO vo=new QueryVO();
        vo.setValue("0");
        vo.setLabel("全部");
        executor.add(0,vo);

        List<QueryVO> taskGroup = this.statisticsMapper.getTaskGroup(projectId);
        vo=new QueryVO();
        vo.setValue("0");
        vo.setLabel("所有任务分组");
        taskGroup.add(0,vo);


        Statistics statistics=new Statistics();
        statistics.setExecutor(executor);
        statistics.setTaskGroup(taskGroup);
        return statistics;
    }




    /*
    * 设置统计条件
    **/
    private StaticDto resultStatic(StaticDto staticDto) {
        if (staticDto.getTaskMember() == null && staticDto.getTaskCase() == null && staticDto.getTaskGroup() == null) {
            staticDto.setDayNum(8);
        } else if (staticDto != null) {
            if ("".equals(staticDto.getTaskMember()) || "0".equals(staticDto.getTaskMember())) {
                staticDto.setTaskMember(null);
            }

            if ("".equals(staticDto.getTaskMember()) || "0".equals(staticDto.getTaskGroup()) || staticDto.getTaskGroup() == "null" ) {
                staticDto.setTaskGroup(null);
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar c = Calendar.getInstance();
            //今天的时间
            String today = format.format(new Date());
            //期间统计的时间计算
            if (StringUtils.isEmpty(staticDto.getStartDay()) || StringUtils.isEmpty(staticDto.getEndDay())) {
                if ("过去7天".equals(staticDto.getTaskDay())) {
                    c.setTime(new Date());
                    c.add(Calendar.DATE, -7);
                    Date d = c.getTime();
                    String day = format.format(d);
                    //将数据写入实体类
                    staticDto.setStartDay(day);
                    staticDto.setEndDay(today);
                    staticDto.setDayNum(8);
                } else if ("过去一个月".equals(staticDto.getTaskDay())) {
                    c.setTime(new Date());
                    c.add(Calendar.MONTH, -1);
                    Date m = c.getTime();
                    String mon = format.format(m);
                    //将数据写入实体类
                    staticDto.setStartDay(mon);
                    staticDto.setEndDay(today);
                    staticDto.setDayNum(30);

                } else if ("过去三个月".equals(staticDto.getTaskDay())) {
                    c.setTime(new Date());
                    c.add(Calendar.MONTH, -3);
                    Date m3 = c.getTime();
                    String mon3 = format.format(m3);
                    //将数据写入实体类
                    staticDto.setStartDay(mon3);
                    staticDto.setEndDay(today);
                    staticDto.setDayNum(90);
                } else {
                    c.setTime(new Date());
                    c.add(Calendar.DATE, -7);
                    Date d = c.getTime();
                    String day = format.format(d);
                    //将数据写入实体类
                    staticDto.setStartDay(day);
                    staticDto.setEndDay(today);
                    staticDto.setDayNum(8);
                }
            } else {
                try {
                    Date firstdate = format.parse(staticDto.getStartDay());
                    Date seconddate = format.parse(staticDto.getEndDay());
                    staticDto.setDayNum(this.longOfTwoDate(firstdate, seconddate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if ("未完成".equals(staticDto.getTaskCase())) {
                staticDto.setTaskCase("未完成");
            } else if ("已完成".equals(staticDto.getTaskCase())) {
                staticDto.setTaskCase("完成");
            } else {
                staticDto.setTaskCase(null);
            }
        }
        return staticDto;
    }


    public static int longOfTwoDate(Date first, Date second) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(first);
        int cnt = 0;
        while (calendar.getTime().compareTo(second) != 0) {
            calendar.add(Calendar.DATE, 1);
            cnt++;
        }
        return cnt;
    }


}
