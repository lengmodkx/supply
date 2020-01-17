package com.art1001.supply.util;

import com.art1001.supply.entity.statistics.StatisticsResultVO;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期数据补全工具类
 *
 * @author lujing
 * @date 2018-09-14 14:22
 */
public class DateUtil {



    //获取累计总任务量
    public  static  List<StatisticsResultVO> getSticList(List<StatisticsResultVO> statisticsResultVOList, int taskCount){
        for (StatisticsResultVO aStatisticsResultVOList : statisticsResultVOList) {
            taskCount = aStatisticsResultVOList.getTaskCountInt() + taskCount;
            aStatisticsResultVOList.setTaskCountAdd(taskCount);
        }
        return statisticsResultVOList;
    }


    public static List<StatisticsResultVO>  getBurnoutList(List<StatisticsResultVO> taskCountList) {
      int i=0,n;
      for (StatisticsResultVO svo : taskCountList) {
          n=taskCountList.size();
          if (svo.getTaskCountAdd()!=0){
              svo.setTaskCountDouble(Double.valueOf(String.format("%.2f", (
                     svo.getTaskCountAdd() - (double) svo.getTaskCountAdd() / (n-1) * i
              ))));
          }else
          {
              svo.setTaskCountDouble((double)0.0);
              n=taskCountList.size()-1;
          }
          i++;
        }
        return  taskCountList;
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

}
