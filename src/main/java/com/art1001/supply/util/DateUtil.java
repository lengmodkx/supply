package com.art1001.supply.util;

import com.art1001.supply.entity.statistics.StatisticsResultVO;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期数据补全工具类
 *
 * @author lujing
 * @date 2018-09-14 14:22
 */
public class DateUtil {


    /**
     * 累计任务补全日期
     *
     * @param statisticsResultVOList 数据库查询出的任务数组
     * @param taskCount              查询日期前的总任务数
     * @param dayNum                 查询天数
     */
    public static Map<String, Integer> dateComplement(List<StatisticsResultVO> statisticsResultVOList, int taskCount, int dayNum) {
        Map<String, Integer> dateMap = dateMap(dayNum);
        for (Map.Entry<String, Integer> entry : dateMap.entrySet()) {
            if (statisticsResultVOList != null && statisticsResultVOList.size() > 0) {
                for (StatisticsResultVO svo : statisticsResultVOList) {
                    if (svo.getCreateTime() != null) {
                        if (svo.getCreateTime().equals(entry.getKey())) {
                            taskCount = (svo.getTaskCountInt() + taskCount);
                            entry.setValue(taskCount);
                        } else {
                            entry.setValue(taskCount);
                        }
                    } else if (svo.getFinishTime() != null) {
                        if (svo.getFinishTime().equals(entry.getKey())) {
                            taskCount = (svo.getTaskCountInt() + taskCount);
                            entry.setValue(taskCount);
                        } else {
                            entry.setValue(taskCount);
                        }
                    }
                }
            } else {
                entry.setValue(taskCount);
            }
        }
        return dateMap;
    }

    /**
     * 每日完成任务补全日期
     *
     * @param statisticsResultVOList 数据库查询出的任务数组
     * @param dayNum                 查询天数
     */
    public static Map<String, Integer> everyDayDateComplement(List<StatisticsResultVO> statisticsResultVOList, int dayNum) {
        Map<String, Integer> dateMap = dateMap(dayNum);
        int i = 0;
        for (Map.Entry<String, Integer> entry : dateMap.entrySet()) {
            if (statisticsResultVOList != null && statisticsResultVOList.size() > 0) {
                for (StatisticsResultVO svo : statisticsResultVOList) {
                        if (entry.getKey().equals(svo.getFinishTime())) {
                            i = svo.getTaskCountInt();
                            entry.setValue(svo.getTaskCountInt());
                        } else {
                            if (i == 0) {
                                entry.setValue(0);
                            } else {
                                entry.setValue(i);
                            }
                        }
                }
            } else {
                entry.setValue(0);
            }
        }
        return dateMap;
    }


    /**
     * 任务燃尽图数据
     *
     * @param dayNum        查询天数
     * @param taskCountMap 累计任务量计算出的天数值
     */
    public static Map taskBurnout(Map<String, Integer> taskCountMap, int dayNum) {
        Map<String, Integer> dateMap = everyDate(dayNum);
        int i = 0;
        if (taskCountMap != null) {
            for (Map.Entry<String, Integer> entry : taskCountMap.entrySet()) {
                if (entry.getValue() != 0) {
                    break;
                }
                dayNum = dayNum - 1;
            }

            for (Map.Entry entryMap : dateMap.entrySet()) {
                for (Map.Entry<String, Integer> entry : taskCountMap.entrySet()) {
                    if (entryMap.getKey().equals(entry.getKey()) && entry.getValue() != 0) {
                        entryMap.setValue(entry.getValue() - (double) entry.getValue() / (dayNum - 1) * i);
                        i++;
                    }
                }

            }
        }
        for (Map.Entry entryMap : dateMap.entrySet()) {
            if (entryMap.getValue()!=null){
                Double parseDouble = Double.parseDouble(entryMap.getValue().toString());
                if (parseDouble.isNaN()) {
                    entryMap.setValue((double) 0.0);
                } else {
                    entryMap.setValue(Double.valueOf(String.format("%.2f", entryMap.getValue())));
                }
            }else {
                entryMap.setValue((double) 0.0);
            }
        }
        return dateMap;
    }

    public static Map<String, Integer> everyDate(int dayNum) {
        return dateMap(dayNum);
    }

    /**
     * 获取天数的map数组  如7天:{2018-09-11=null, 2018-09-12=null, 2018-09-13=null, 2018-09-14=null, 2018-09-15=null, 2018-09-16=null, 2018-09-17=null}
     **/
    private static Map<String, Integer> dateMap(int dayNum) {
        List<String> daysList = pastDaysList(dayNum);
        Map<String, Integer> map = new TreeMap<>();
        for (String day : daysList) {
            map.put(day, null);
        }
        return map;
    }

    //获取每天创建任务的集合
    public  static Map<String, Integer> createTask(List<StatisticsResultVO> statisticsResultVOList,int dayNum) {
        Map<String, Integer> dateMap = everyDate(dayNum);
        for (Map.Entry map : dateMap.entrySet()) {
            for (StatisticsResultVO svo : statisticsResultVOList) {
                if (map.getKey().equals(svo.getCreateTime())){
                    map.setValue(svo.getTaskCountInt());
                }
            }
            if (map.getValue()==null){
                map.setValue(0);
            }
        }
        return dateMap;
    }


    private static ArrayList<String> pastDaysList(int intervals) {
        ArrayList<String> pastDaysList = new ArrayList<>();
        for (int i = 0; i < intervals; i++) {
            pastDaysList.add(getPastDate(i));
        }
        return pastDaysList;
    }

    private static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(today);
    }


    //获取明天的日期
    public static String getNextDay(String Day) {
     DateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
     try {
         Date temp = dft.parse(Day);
         Calendar cld = Calendar.getInstance();
         cld.setTime(temp);
         cld.add(Calendar.DATE, 1);
         temp = cld.getTime();
         //获得下一天日期字符串
         String nextDay = dft.format(temp);
         return  nextDay;
     } catch (ParseException e) {
         e.printStackTrace();
     }
       return null;
    }

 //获取昨天的日期
    public static String getYesterday(String Day) {
     DateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
     try {
         Date temp = dft.parse(Day);
         Calendar cld = Calendar.getInstance();
         cld.setTime(temp);
         cld.add(Calendar.DATE, -1);
         temp = cld.getTime();
         //获得昨天日期字符串
         String yesterday = dft.format(temp);
         return  yesterday;
     } catch (ParseException e) {
         e.printStackTrace();
     }
       return null;
    }




}
