package com.art1001.supply.service.statistics.impl;

import com.art1001.supply.entity.statistics.*;
import com.art1001.supply.mapper.statistics.StaticInfoMapper;
import com.art1001.supply.service.statistics.StatisticsService;
import com.art1001.supply.util.CalculateProportionUtil;
import com.art1001.supply.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    private StaticInfoMapper statisticsMapper;

    /**
     * 查询未完成和已完成时的传值
     */
    private static final String UNFINISH_TASK_CASE = "未完成";

    private static final String FINISH_TASK_CASE = "完成";



    /**
     * 查询出该项目下的所有任务 状态数量概览
     * (多次连库查询比筛选集合效率略高 所以 使用多次按照关键字查库的方式 取出数据)
     * @param projectId 项目id
     * @return
     */
    public List<QueryVO> findTaskCountOverView(String projectId) {
        int total = statisticsMapper.getCountTask(projectId);
        String[] overViewName = {"任务总量","已完成","未完成","已逾期","待认领","按时完成","今日到期",  "逾期完成"};
        List<QueryVO> list = new ArrayList<QueryVO>();
        List<String> chartsList = new ArrayList<String>();
        int taskCount=0;
        for (String names : overViewName) {

            QueryVO queryVO = new QueryVO();
            queryVO.setValue(names);


            if(StaticticsVO.HANGINTHEAIR.equals(names)){
                //查询出未完成的任务数量
                taskCount = statisticsMapper.findHangInTheAirTaskCount(projectId);
            }else if(StaticticsVO.COMPLETED.equals(names)) {
                //查询出已完成的任务
                taskCount = statisticsMapper.findCompletedTaskCount(projectId);
            }else  if(StaticticsVO.MATURINGTODAY.equals(names)){
                //查询出今日到期的任务
                taskCount = statisticsMapper.currDayTaskCount(projectId,System.currentTimeMillis()/1000);

            }else if(StaticticsVO.BEOVERDUE.equals(names)){
                //查询出已逾期的任务
                taskCount = statisticsMapper.findBeoberdueTaskCount(projectId,System.currentTimeMillis()/1000);

            }else if(StaticticsVO.TOBECLAIMED.equals(names)){
                //查询出待认领的任务
                taskCount = statisticsMapper.findTobeclaimedTaskCount(projectId);

            }else  if(StaticticsVO.FINISHONTIME.equals(names)){
                //查询出按时完成的任务
                taskCount = statisticsMapper.findFinishontTimeTaskCount(projectId,System.currentTimeMillis()/1000);

            }else if(StaticticsVO.OVERDUECOMPLETION.equals(names)){
                //查询出逾期完成任务
                taskCount = statisticsMapper.findOverdueCompletion(projectId,System.currentTimeMillis()/1000);
            }

                if(!StaticticsVO.TASKTOTALCOUNT.equals(names)){
                    //设置百分比
                    NumberFormat numberFormat = NumberFormat.getInstance();
                    numberFormat.setMaximumFractionDigits(0);
                    //设置该组的达标数量
                    queryVO.setLabel(String.valueOf(taskCount));
                    Double value = (double) taskCount / (double) total * 100;
                    if (!value.isNaN()){
                        queryVO.setPercent(Double.valueOf(numberFormat.format(value)));
                    }else{
                        queryVO.setPercent((double)0);
                    }
                }else{
                    queryVO.setLabel(String.valueOf(total));
                    queryVO.setPercent((double)100);
                }
            list.add(queryVO);
        }
        return list;
    }



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
       /* for (int i=0;i<statisticsPies.size();i++) {
            String num = CalculateProportionUtil.proportionDouble( Float.valueOf(statisticsPies.get(i).getY().toString()),(float)count, 2);
            statisticsPies.get(i).setY(Float.valueOf(num));
        }*/
        return  statisticsPies;
    }


    /**
     * 获取柱状图数据
     */
    @Override
    public StatisticsHistogram getHistogramsChart(String projectId,StaticDto sto) {

        //系统当前时间
        Long currentDate = System.currentTimeMillis() / 1000;

        sto =  resultStatic(sto);

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

         Map<String, Integer> dayNumMap = DateUtil.everyDate(sto.getDayNum());
        try {
            //获取七天的总数据
            List<StatisticsResultVO> statisticsResultVOList = this.statisticsMapper.selectTaskBurnOut(projectId,sto);

            Integer count = this.statisticsMapper.taskSevenDayAgo(projectId, currentDate, sto.getDayNum());
            //项目进展走势
            List<StatisticsResultVO> taskOfProgress = this.statisticsMapper.taskOfProgress(projectId, currentDate,sto,dayNumMap);
           //计算每天创建的任务量
            Map<String, Integer> daysTaskMap = DateUtil.createTask(taskOfProgress,sto.getDayNum());
            //计算累计任务量
            Map<String, Integer> taskCountMap = DateUtil.dateComplement(taskOfProgress, count, sto.getDayNum());
            Map<String, Double> stringMap = DateUtil.taskBurnout(taskCountMap, sto.getDayNum());

            List<StatisticsResultVO> taskOfFinishProgress = this.statisticsMapper.taskOfFinishProgress(projectId, currentDate,sto);
            //跟据上面查询的taskOfFinishedProgress列表计算每日完成任务量
            Map<String, Integer> taskEveryDayMap = DateUtil.everyDayDateComplement(taskOfFinishProgress, sto.getDayNum());

            // type = 0 时包含所有数据  type = 1   燃尽图数据  type = 2  累计图数据
            if (type == 0){

               statisticsBurnout=null;
                       //this.getStatisAllData(taskCountMap,daysTaskMap,projectId,sto,taskOfFinishProgress,stringMap);


            }else if(type == 1){
               statisticsBurnout.setSticResultVOS(statisticsResultVOList);
               statisticsBurnout=this.getBurnOut(daysTaskMap,taskOfFinishProgress,stringMap);

            }else if(type == 2){

                statisticsBurnout=this.getCumulative(taskCountMap,taskOfFinishProgress,projectId,sto,stringMap);
            }


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

    @Override
    public Statistics getCountTable(String divName, String projectId, StatisticsDTO statisticsDTO) {


        if (divName != null && !"".equals(divName)) {
            if (StaticticsVO.HANGINTHEAIR.equals(divName)) {
                return this.selectUnfinishedTask(statisticsDTO, projectId);
            } else if (StaticticsVO.COMPLETED.equals(divName)) {
                return this.selectFinishedTask(statisticsDTO, projectId);
            }  else if (StaticticsVO.MATURINGTODAY.equals(divName)) {
                return this.selectExpireTask(statisticsDTO, projectId);
            } else if (StaticticsVO.BEOVERDUE.equals(divName)) {
                return this.selectOverdueTask(statisticsDTO, projectId);
            } else if (StaticticsVO.TOBECLAIMED.equals(divName)) {
                return this.selectWaitClaimTask(statisticsDTO, projectId);
            } else if (StaticticsVO.FINISHONTIME.equals(divName)) {
                return this.selectPunctualityTask(statisticsDTO, projectId);
            } else if (StaticticsVO.OVERDUECOMPLETION.equals(divName)) {
                return this.selectExpiredToCompleteTask(statisticsDTO, projectId);
            }
        } else {
            System.out.println(">>>>>>>>>divName=" + divName + "statisticsDTO=" + statisticsDTO + ">>>>>>>");
            throw new NullPointerException();
        }
        return null;
    }

    /**
     * 获取统计页面的图数据和表数据
     * @param projectId
     * @param sto
     * @return
     */
    @Override
    public List<StatisticsHistogram> getHistogramsData(String projectId, StaticDto sto) {

        //系统当前时间
        Long currentDate = System.currentTimeMillis() / 1000;

        sto =  resultStatic(sto);

        //获取每个用户的数据
        List<StatisticsHistogram> statisticsHistograms=statisticsMapper.getHistogramsDate(projectId,currentDate,sto);
        return statisticsHistograms;
    }


    /**
         * 未完成数据
         */
    private Statistics selectUnfinishedTask(StatisticsDTO statisticsDTO, String projectId) {
        Statistics statistics=new Statistics();
        try {
            // 总任务数
            int count = this.statisticsMapper.getCountTask(projectId);
            //查询未完成数据
            List<StatisticsResultVO> statisticsResultVOList = this.statisticsMapper.selectUnfinishTask(statisticsDTO, UNFINISH_TASK_CASE, projectId);
            //String percent = numChange((double) statisticsResultVOList.size() / (double) count);

            //表格数据
            ArrayList<TitleVO> arrayList=new ArrayList<>();
            TitleVO title=new TitleVO("截止时间","endTime");
            arrayList.add(title);
            title=new TitleVO("任务","taskName");
            arrayList.add(title);
            title=new TitleVO("执行者","executor");
            arrayList.add(title);
            title=new TitleVO("任务分组","taskGroup");
            arrayList.add(title);
            title=new TitleVO("列表","listView");
            arrayList.add(title);

            statistics.setTitleList(arrayList);

            statistics.setSticsResultList(statisticsResultVOList);

        } catch (Exception e) {
            System.out.println("---未完成报错---");
            statistics=null;
            e.printStackTrace();
        }
        return statistics;
    }




    /**
     * 已完成数据
     */
    private Statistics selectFinishedTask(StatisticsDTO statisticsDTO, String projectId) {
        Statistics statistics=new Statistics();
        try {
            //总任务数
            int count = this.statisticsMapper.getCountTask(projectId);
            //查询已完成任务
            List<StatisticsResultVO> statisticsResultVOList = this.statisticsMapper.selectFinishTask(statisticsDTO, FINISH_TASK_CASE, projectId);
            //String percent = numChange((double) statisticsResultVOList.size() / (double) count);

            //表格数据
            ArrayList<TitleVO> arrayList=new ArrayList<>();
            TitleVO title=new TitleVO("完成时间","finishTime");
            arrayList.add(title);
            title=new TitleVO("任务","taskName");
            arrayList.add(title);
            title=new TitleVO("执行者","executor");
            arrayList.add(title);
            title=new TitleVO("任务分组","taskGroup");
            arrayList.add(title);
            title=new TitleVO("列表","listView");
            arrayList.add(title);

            statistics.setTitleList(arrayList);
            statistics.setSticsResultList(statisticsResultVOList);

        } catch (Exception e) {
            System.out.println("---已完成报错---");
            statistics = null;
            e.printStackTrace();
        }
        return statistics;
    }



    /**
     * 今日到期
     */
    private Statistics selectExpireTask(StatisticsDTO statisticsDTO, String projectId) {
        Statistics statistics=new Statistics();
        try {
            //总任务数
            int count = this.statisticsMapper.getCountTask(projectId);
            //查询今日到期的数据
            List<StatisticsResultVO> statisticsResultVOList = this.statisticsMapper.selectExpireTask(statisticsDTO, projectId);
            //String percent = numChange((double) statisticsResultVOList.size() / (double) count);

            //表格数据
            ArrayList<TitleVO> arrayList=new ArrayList<>();
            TitleVO title=new TitleVO("创建时间","createTime");
            arrayList.add(title);
            title=new TitleVO("任务","taskName");
            arrayList.add(title);
            title=new TitleVO("执行者","executor");
            arrayList.add(title);
            title=new TitleVO("任务分组","taskGroup");
            arrayList.add(title);
            title=new TitleVO("列表","listView");
            arrayList.add(title);

            statistics.setTitleList(arrayList);
            statistics.setSticsResultList(statisticsResultVOList);

        } catch (Exception e) {
            System.out.println("今日到期报错");
            statistics = null;
            e.printStackTrace();
        }
        return statistics;
    }

    /**
     * 已逾期
     */
    private Statistics selectOverdueTask(StatisticsDTO statisticsDTO, String projectId) {
        Statistics statistics=new Statistics();
        try {
            //总任务数
            int count = this.statisticsMapper.getCountTask(projectId);
            //已逾期数据
            List<StatisticsResultVO> statisticsResultVOList = this.statisticsMapper.selectOverdueTask(statisticsDTO, projectId);
            //String percent = numChange((double) statisticsResultVOList.size() / (double) count);

            //表格数据
            ArrayList<TitleVO> arrayList=new ArrayList<>();
            TitleVO title=new TitleVO("截止时间","endTime");
            arrayList.add(title);
            title=new TitleVO("任务","taskName");
            arrayList.add(title);
            title=new TitleVO("执行者","executor");
            arrayList.add(title);
            title=new TitleVO("任务分组","taskGroup");
            arrayList.add(title);
            title=new TitleVO("列表","listView");
            arrayList.add(title);

            statistics.setTitleList(arrayList);
            statistics.setSticsResultList(statisticsResultVOList);

        } catch (Exception e) {
            System.out.println("已逾期报错");
            statistics = null;
            e.printStackTrace();
        }
        return statistics;
    }

    /**
     * 待认领
     */
    private Statistics selectWaitClaimTask(StatisticsDTO statisticsDTO, String projectId) {
        Statistics statistics=new Statistics();
        try {
            //总任务数
            int count = this.statisticsMapper.getCountTask(projectId);
            //待认领数据
            List<StatisticsResultVO> statisticsResultVOList = this.statisticsMapper.selectWaitClaimTask(statisticsDTO, projectId);
            //String percent = numChange((double) statisticsResultVOList.size() / (double) count);

            //表格数据
            ArrayList<TitleVO> arrayList=new ArrayList<>();
            TitleVO title=new TitleVO("创建时间","createTime");
            arrayList.add(title);
            title=new TitleVO("任务","taskName");
            arrayList.add(title);
            title=new TitleVO("任务分组","taskGroup");
            arrayList.add(title);
            title=new TitleVO("列表","listView");
            arrayList.add(title);

            statistics.setTitleList(arrayList);
            statistics.setSticsResultList(statisticsResultVOList);

        } catch (Exception e) {
            System.out.println("待认领报错");
            statistics = null;
            e.printStackTrace();
        }
        return statistics;

    }

    /**
     * 按时完成数据
     */
    private Statistics selectPunctualityTask(StatisticsDTO statisticsDTO, String projectId) {
        Statistics statistics=new Statistics();
        try {
            //总任务数
            int count = this.statisticsMapper.getCountTask(projectId);
            //按时完成数据
            List<StatisticsResultVO> statisticsResultVOList = this.statisticsMapper.selectPunctualityTask(statisticsDTO, projectId);
            //String percent = numChange((double) statisticsResultVOList.size() / (double) count);

            //表格数据
            ArrayList<TitleVO> arrayList=new ArrayList<>();
            TitleVO title=new TitleVO("完成时间","finishTime");
            arrayList.add(title);
            title=new TitleVO("任务","taskName");
            arrayList.add(title);
            title=new TitleVO("执行者","executor");
            arrayList.add(title);
            title=new TitleVO("任务分组","taskGroup");
            arrayList.add(title);
            title=new TitleVO("列表","listView");
            arrayList.add(title);

            statistics.setTitleList(arrayList);
            statistics.setSticsResultList(statisticsResultVOList);

        } catch (Exception e) {
            System.out.println("按时完成报错");
            statistics = null;
            e.printStackTrace();
        }
        return statistics;
    }

    /**
     * 逾期完成数据
     */
    private Statistics selectExpiredToCompleteTask(StatisticsDTO statisticsDTO, String projectId) {
        Statistics statistics=new Statistics();
        try {
            //总任务数
            int count = this.statisticsMapper.getCountTask(projectId);
            List<StatisticsResultVO> statisticsResultVOList = this.statisticsMapper.selectExpiredToCompleteTask(statisticsDTO, projectId);
            //String percent = numChange((double) statisticsResultVOList.size() / (double) count);
            List<String> titleList = new ArrayList<>();
            titleList.add("完成时间");
            titleList.add("任务");
            titleList.add("执行者");
            titleList.add("任务分组");
            titleList.add("列表");
            titleList.add("逾期天数");


            //表格数据
            ArrayList<TitleVO> arrayList=new ArrayList<>();
            TitleVO title=new TitleVO("完成时间","finishTime");
            arrayList.add(title);
            title=new TitleVO("任务","taskName");
            arrayList.add(title);
            title=new TitleVO("执行者","executor");
            arrayList.add(title);
            title=new TitleVO("任务分组","taskGroup");
            arrayList.add(title);
            title=new TitleVO("列表","listView");
            arrayList.add(title);
            title=new TitleVO("逾期天数","overdueNum");
            arrayList.add(title);

            statistics.setTitleList(arrayList);
            statistics.setSticsResultList(statisticsResultVOList);

        } catch (Exception e) {
            System.out.println("逾期完成报错");
            statistics = null;
            e.printStackTrace();
        }
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
                if ("7".equals(staticDto.getTaskDay())) {
                    c.setTime(new Date());
                    c.add(Calendar.DATE, -8);
                    Date d = c.getTime();
                    String day = format.format(d);
                    //将数据写入实体类
                    staticDto.setStartDay(day);
                    staticDto.setEndDay(today);
                    staticDto.setDayNum(8);
                } else if ("30".equals(staticDto.getTaskDay())) {
                    c.setTime(new Date());
                    c.add(Calendar.MONTH, -1);
                    Date m = c.getTime();
                    String mon = format.format(m);
                    //将数据写入实体类
                    staticDto.setStartDay(mon);
                    staticDto.setEndDay(today);
                    staticDto.setDayNum(30);

                } else if ("90".equals(staticDto.getTaskDay())) {
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
                    c.add(Calendar.DATE, -8);
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


    /**
     * 将小数转换为百分比
     */
    private String numChange(double num) {
        //获取格式化对象
        NumberFormat nt = NumberFormat.getInstance();
        //设置百分数精确度2即保留两位小数
        nt.setMaximumFractionDigits(2);
        return nt.format(num * 100);
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


    //获取进入统计页面的所有数据
    private StatisticsBurnout  getStatisAllData(Map<String, Integer> taskCountMap,Map<String, Integer> createTaskMap,String projectId,StaticDto sto,List<StatisticsResultVO> taskOfFinishProgress,Map<String, Double> stringMap){
        //系统当前时间
        Long currentDate = System.currentTimeMillis() / 1000;
        StatisticsBurnout statisticsBurnout = new StatisticsBurnout();
        //项目进展走势
        Integer[] firstArray = new Integer[sto.getDayNum()];
        int i = 0;
        if (taskCountMap != null && taskCountMap.size() > 0) {
            for (Map.Entry<String, Integer> entry : taskCountMap.entrySet()) {
                firstArray[i] = entry.getValue();
                i++;
            }
        }

        //获取七天前的完成任务量
        int finishCount = this.statisticsMapper.taskFinishOfSevenDayAgo(projectId, currentDate,sto);
        Map<String, Integer> finishMap = DateUtil.dateComplement(taskOfFinishProgress, finishCount, sto.getDayNum());
        Integer[] secondArray = new Integer[sto.getDayNum()];
        i = 0;
        for (Map.Entry<String, Integer> entry : finishMap.entrySet()) {
            secondArray[i] = entry.getValue();
            i++;
        }

        statisticsBurnout.setCumulativeTask(firstArray);
        statisticsBurnout.setCompletionTask(secondArray);
        String[] everyDateName = new String[sto.getDayNum()];
        //燃尽图数据
        i = 0;
        Double[] everyDateInt = new Double[sto.getDayNum()];
        if (stringMap != null && stringMap.size() > 0) {
            for (Map.Entry<String, Double> entry : stringMap.entrySet()) {
                everyDateInt[i] = entry.getValue();
                everyDateName[i] = entry.getKey();
                i++;
            }
        }
        i = 0;
        //将时间段内每天完成的任务转换成Map格式  key对应时间，value对应任务数
        Map<String, Integer> Map = DateUtil.everyDate(sto.getDayNum());
        for (Map.Entry<String, Integer> entry : Map.entrySet()) {
            if (entry.getValue()==null){
                entry.setValue(0);
            }
            for (StatisticsResultVO svo : taskOfFinishProgress) {
                if (svo.getFinishTime()!=null && svo.getFinishTime().equals(entry.getKey())){
                    entry.setValue(svo.getTaskCountInt());
                }
            }
        };
        Integer[] secondInt = new Integer[Map.size()];
        //将时间段内总任务与时间段内完成任务相减，获得剩余任务数
     /*   if (Map != null && Map.size() > 0) {
            for (Map.Entry<String, Integer> entry : taskCountMap.entrySet()) {
                if (taskCountMap != null && taskCountMap.size() > 0) {
                    for (Map.Entry<String, Integer> everyEntry : Map.entrySet()) {
                        if (everyEntry.getKey().equals(entry.getKey())) {
                            if (everyEntry.getValue()==0 && entry.getValue() ==0){
                                secondInt[i]=0;
                                i++;
                            }else if (i<taskCountMap.size()-1){
                                secondInt[i]=entry.getValue()-everyEntry.getValue();
                                taskCountMap.put(DateUtil.getNextDay(entry.getKey()),secondInt[i]);
                                i++;
                            }else {
                                taskCountMap.put(entry.getKey(),taskCountMap.get(DateUtil.getYesterday(entry.getKey())));
                                secondInt[i]=entry.getValue()-everyEntry.getValue();
                                i++;
                            }
                        }
                    }
                }
            }
        }*/
      if (Map != null && Map.size() > 0) {
            for (Map.Entry<String, Integer> creatEentry : createTaskMap.entrySet()) {
                    for (Map.Entry<String, Integer> everyEntry : Map.entrySet()) {
                        if (creatEentry.getKey().equals(everyEntry.getKey())) {
                            if (everyEntry.getValue()==0 && creatEentry.getValue() ==0){
                                secondInt[i]=0;
                                i++;
                            }else if (i<createTaskMap.size()-1){
                                secondInt[i]=creatEentry.getValue()-everyEntry.getValue();
                                createTaskMap.put(DateUtil.getNextDay(creatEentry.getKey()),secondInt[i]+createTaskMap.get(DateUtil.getNextDay(creatEentry.getKey())));
                                i++;
                            }else if(i==createTaskMap.size()-1){
                                secondInt[i]=creatEentry.getValue()-everyEntry.getValue();
                                i++;
                            }

                        }
                    }
            }
        }
        statisticsBurnout.setTrueTask(secondInt);
        statisticsBurnout.setIdealTask(everyDateInt);
        statisticsBurnout.setEveryDate(everyDateName);
        return statisticsBurnout;
    }


    private  StatisticsBurnout getBurnOut(Map<String, Integer> createTaskMap,List<StatisticsResultVO> taskOfFinishProgress,Map<String, Double> stringMap){

        StatisticsBurnout statisticsBurnout = new StatisticsBurnout();
        String[] everyDateName = new String[stringMap.size()];
        //燃尽图数据
        int i = 0;
        Double[] everyDateInt = new Double[stringMap.size()];
        if (stringMap != null && stringMap.size() > 0) {
            for (Map.Entry<String, Double> entry : stringMap.entrySet()) {
                everyDateInt[i] = entry.getValue();
                everyDateName[i] = entry.getKey();
                i++;
            }
        }
        i = 0;
        //将时间段内每天完成的任务转换成Map格式  key对应时间，value对应任务数
        Map<String, Integer> Map = DateUtil.everyDate(createTaskMap.size());
        for (Map.Entry<String, Integer> entry : Map.entrySet()) {
            if (entry.getValue()==null){
                entry.setValue(0);
            }
            for (StatisticsResultVO svo : taskOfFinishProgress) {
                if (svo.getFinishTime()!=null && svo.getFinishTime().equals(entry.getKey())){
                    entry.setValue(svo.getTaskCountInt());
                }
            }
        };
        Integer[] secondInt = new Integer[Map.size()];
        //将时间段内总任务与时间段内完成任务相减，获得剩余任务数
        if (Map != null && Map.size() > 0) {
            for (Map.Entry<String, Integer> creatEentry : createTaskMap.entrySet()) {
                for (Map.Entry<String, Integer> everyEntry : Map.entrySet()) {
                    if (creatEentry.getKey().equals(everyEntry.getKey())) {
                        if (everyEntry.getValue()==0 && creatEentry.getValue() ==0){
                            secondInt[i]=0;
                            i++;
                        }else if (i<createTaskMap.size()-1){
                            secondInt[i]=creatEentry.getValue()-everyEntry.getValue();
                            createTaskMap.put(DateUtil.getNextDay(creatEentry.getKey()),secondInt[i]+createTaskMap.get(DateUtil.getNextDay(creatEentry.getKey())));
                            i++;
                        }else if(i==createTaskMap.size()-1){
                            secondInt[i]=creatEentry.getValue()-everyEntry.getValue();
                            i++;
                        }

                    }
                }
            }
        }
        statisticsBurnout.setTrueTask(secondInt);
        statisticsBurnout.setIdealTask(everyDateInt);
        statisticsBurnout.setEveryDate(everyDateName);
        return statisticsBurnout;
    }

   //获取累计图数据
    private  StatisticsBurnout  getCumulative(Map<String, Integer> taskCountMap,List<StatisticsResultVO> taskOfFinishProgress,String projectId,StaticDto sto,Map<String, Double> stringMap){

        Long currentDate = System.currentTimeMillis() / 1000;
        StatisticsBurnout statisticsBurnout = new StatisticsBurnout();
        String[] everyDateName = new String[stringMap.size()];
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
        statisticsBurnout.setEveryDate(everyDateName);

        return  statisticsBurnout;
    }


}
