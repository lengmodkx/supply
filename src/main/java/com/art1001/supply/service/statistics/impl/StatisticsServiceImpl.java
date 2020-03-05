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
     * @param sto
     * @return
     */
    public List<QueryVO> findTaskCountOverView(String projectId, StaticDto sto) {
        sto = resultStatic(sto);
        int total = statisticsMapper.getCountTask(projectId, sto);
        String[] overViewName = {"任务总量","已完成","未完成","已逾期","待认领","按时完成","今日到期",  "逾期完成"};
        List<QueryVO> list = new ArrayList<QueryVO>();
        List<String> chartsList = new ArrayList<String>();
        int taskCount=0;
        for (String names : overViewName) {

            QueryVO queryVO = new QueryVO();
            queryVO.setValue(names);


            if(StaticticsVO.HANGINTHEAIR.equals(names)){
                //查询出未完成的任务数量
                taskCount = statisticsMapper.findHangInTheAirTaskCount(projectId,sto);
            }else if(StaticticsVO.COMPLETED.equals(names)) {
                //查询出已完成的任务
                taskCount = statisticsMapper.findCompletedTaskCount(projectId,sto);
            }else  if(StaticticsVO.MATURINGTODAY.equals(names)){
                //查询出今日到期的任务
                taskCount = statisticsMapper.currDayTaskCount(projectId,System.currentTimeMillis()/1000,sto);

            }else if(StaticticsVO.BEOVERDUE.equals(names)){
                //查询出已逾期的任务
                taskCount = statisticsMapper.findBeoberdueTaskCount(projectId,System.currentTimeMillis()/1000,sto);

            }else if(StaticticsVO.TOBECLAIMED.equals(names)){
                //查询出待认领的任务
                taskCount = statisticsMapper.findTobeclaimedTaskCount(projectId,sto);

            }else  if(StaticticsVO.FINISHONTIME.equals(names)){
                //查询出按时完成的任务
                taskCount = statisticsMapper.findFinishontTimeTaskCount(projectId,System.currentTimeMillis()/1000,sto);

            }else if(StaticticsVO.OVERDUECOMPLETION.equals(names)){
                //查询出逾期完成任务
                taskCount = statisticsMapper.findOverdueCompletion(projectId,System.currentTimeMillis()/1000,sto);
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
       sto = resultStatic(sto);
        return statisticsMapper.getCountTask(projectId,sto);
    }

    /**
     * 获取饼图数据
     */
    @Override
    public List<StatisticsPie> getPieChart(String projectId, Integer count, StaticDto sto) {
        sto = resultStatic(sto);
        //获取每个用户的任务数
        List<StatisticsPie> statisticsPies=statisticsMapper.getPieDate(projectId,sto);
        statisticsPies=getPieSource(statisticsPies);
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
        sto = resultStatic(sto);
        //获取每个用户的任务数
        List<StatisticsPie> statisticsPies=statisticsMapper.selectExcutorTask(projectId,resultStatic(sto));
        statisticsPies=getPieSource(statisticsPies);
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

        int noUserInt=0;
        for (int i=0;i<statisticsHistograms.size();i++) {
            if (statisticsHistograms.get(i).getName().equals("待认领")){
                noUserInt+=statisticsHistograms.get(i).getData();
                statisticsHistograms.remove(i);
                i--;
            }
        }
        if (noUserInt!=0){
            StatisticsHistogram statisticsHistogram = new StatisticsHistogram();
            statisticsHistogram.setName("待认领");
            statisticsHistogram.setData(noUserInt);
            statisticsHistograms.add(statisticsHistogram);
        }

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
            //List<StatisticsResultVO> statisticsResultVOList = this.statisticsMapper.selectTaskBurnOut(projectId,sto);
            Integer count = this.statisticsMapper.taskSevenDayAgo(projectId, currentDate, sto.getDayNum());
            //项目进展走势
            List<StatisticsResultVO> taskOfProgress = this.statisticsMapper.taskOfProgress(projectId, currentDate,sto,dayNumMap);
            //重写之后的list数据  累计总任务
            List<StatisticsResultVO> taskCountList=DateUtil.getSticList(taskOfProgress, count);
            taskCountList=DateUtil.getBurnoutList(taskCountList);

            List<StatisticsResultVO> taskOfFinishProgress = this.statisticsMapper.taskOfFinishProgress(projectId, currentDate,sto,dayNumMap);


            // type = 0 时包含所有数据  type = 1   燃尽图数据  type = 2  累计图数据
            if (type == 0){
                statisticsBurnout = this.getSticAllData(taskCountList, taskOfFinishProgress, projectId,sto);

            }else if(type == 1){
               statisticsBurnout=this.getBurnout(taskCountList,taskOfFinishProgress);
               statisticsBurnout.setSticResultVOS(this.statisticsMapper.selectTaskBurnOut(projectId,sto));

            }else if(type == 2){
                statisticsBurnout=this.getCumulative(taskCountList,taskOfFinishProgress,projectId,sto);
                statisticsBurnout.setSticResultVOS(this.statisticsMapper.selectProjectProgress(projectId,sto));
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
    public Statistics getCountTable(String divName, String projectId, StaticDto sto) {

        sto =  resultStatic(sto);


        if (divName != null && !"".equals(divName)) {
            if (StaticticsVO.HANGINTHEAIR.equals(divName)) {
                return this.selectUnfinishedTask(sto, projectId);
            } else if (StaticticsVO.COMPLETED.equals(divName)) {
                return this.selectFinishedTask(sto, projectId);
            }  else if (StaticticsVO.MATURINGTODAY.equals(divName)) {
                return this.selectExpireTask(sto, projectId);
            } else if (StaticticsVO.BEOVERDUE.equals(divName)) {
                return this.selectOverdueTask(sto, projectId);
            } else if (StaticticsVO.TOBECLAIMED.equals(divName)) {
                return this.selectWaitClaimTask(sto, projectId);
            } else if (StaticticsVO.FINISHONTIME.equals(divName)) {
                return this.selectPunctualityTask(sto, projectId);
            } else if (StaticticsVO.OVERDUECOMPLETION.equals(divName)) {
                return this.selectExpiredToCompleteTask(sto, projectId);
            }
        } else {

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
        int noUserInt=0;
        for (int i=0;i<statisticsHistograms.size();i++) {
            if (statisticsHistograms.get(i).getName().equals("待认领")){
                noUserInt+=statisticsHistograms.get(i).getData();
                statisticsHistograms.remove(i);
                i--;
            }
        }
        if (noUserInt!=0){
            StatisticsHistogram statisticsHistogram = new StatisticsHistogram();
            statisticsHistogram.setName("待认领");
            statisticsHistogram.setData(noUserInt);
            statisticsHistograms.add(statisticsHistogram);
        }
        return statisticsHistograms;
    }


    /**
         * 未完成数据
         */
    private Statistics selectUnfinishedTask(StaticDto sto, String projectId) {

        sto =  resultStatic(sto);
        Statistics statistics=new Statistics();
        try {
            // 总任务数
            int count = this.statisticsMapper.getCountTask(projectId, sto);
            //查询未完成数据
            List<StatisticsResultVO> statisticsResultVOList = this.statisticsMapper.selectUnfinishTask(sto, UNFINISH_TASK_CASE, projectId);
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
    private Statistics selectFinishedTask(StaticDto sto, String projectId) {
        Statistics statistics=new Statistics();
        sto =  resultStatic(sto);
        try {
            //总任务数
            int count = this.statisticsMapper.getCountTask(projectId, sto);
            //查询已完成任务
            List<StatisticsResultVO> statisticsResultVOList = this.statisticsMapper.selectFinishTask(sto, FINISH_TASK_CASE, projectId);
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
    private Statistics selectExpireTask(StaticDto sto, String projectId) {
        Statistics statistics=new Statistics();
        sto =  resultStatic(sto);
        try {
            //总任务数
            int count = this.statisticsMapper.getCountTask(projectId, sto);
            //查询今日到期的数据
            List<StatisticsResultVO> statisticsResultVOList = this.statisticsMapper.selectExpireTask(sto, projectId);
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
    private Statistics selectOverdueTask(StaticDto sto, String projectId) {
        Statistics statistics=new Statistics();
        sto =  resultStatic(sto);
        try {
            //总任务数
            int count = this.statisticsMapper.getCountTask(projectId, sto);
            //已逾期数据
            List<StatisticsResultVO> statisticsResultVOList = this.statisticsMapper.selectOverdueTask(sto, projectId);
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
    private Statistics selectWaitClaimTask(StaticDto sto, String projectId) {
        Statistics statistics=new Statistics();
        sto =  resultStatic(sto);
        try {
            //总任务数
            int count = this.statisticsMapper.getCountTask(projectId, sto);
            //待认领数据
            List<StatisticsResultVO> statisticsResultVOList = this.statisticsMapper.selectWaitClaimTask(sto, projectId);
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
    private Statistics selectPunctualityTask(StaticDto sto, String projectId) {
        Statistics statistics=new Statistics();
        sto =  resultStatic(sto);
        try {
            //总任务数
            int count = this.statisticsMapper.getCountTask(projectId, sto);
            //按时完成数据
            List<StatisticsResultVO> statisticsResultVOList = this.statisticsMapper.selectPunctualityTask(sto, projectId);
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
    private Statistics selectExpiredToCompleteTask(StaticDto sto, String projectId) {
        Statistics statistics=new Statistics();
        sto =  resultStatic(sto);
        try {
            //总任务数
            int count = this.statisticsMapper.getCountTask(projectId, sto);
            List<StatisticsResultVO> statisticsResultVOList = this.statisticsMapper.selectExpiredToCompleteTask(sto, projectId);
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
            staticDto.setDayNum(7);
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
                    staticDto.setDayNum(7);
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
                    staticDto.setDayNum(7);
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
                staticDto.setTaskCondition(0);
            } else if ("已完成".equals(staticDto.getTaskCase())) {
                staticDto.setTaskCondition(1);
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
    private StatisticsBurnout  getSticAllData(List<StatisticsResultVO> taskCountList, List<StatisticsResultVO> taskFinishList, String projectId, StaticDto sto){

        sto =  resultStatic(sto);

        //系统当前时间
        Long currentDate = System.currentTimeMillis() / 1000;
        int n=taskCountList.size();
        StatisticsBurnout statisticsBurnout = new StatisticsBurnout();



        int finishCount=this.statisticsMapper.taskFinishOfSevenDayAgo(projectId, currentDate,null);

        //项目进展走势
        Integer[] firstArray = new Integer[n];
        Integer[] secondArray = new Integer[n];
        String[] everyDateName = new String[n];
        Double[] everyDateInt = new Double[n];
        Integer[] secondInt = new Integer[n];

        int min=taskCountList.get(0).getTaskCountInt();

        if (!taskCountList.isEmpty()){
            for (int i=0;i<n;i++){
                firstArray[i] = taskCountList.get(i).getTaskCountAdd();
                secondArray[i] = taskFinishList.get(i).getTaskCountInt()+finishCount;
                finishCount=secondArray[i];
                everyDateName[i]=taskCountList.get(i).getCreateTime();
                everyDateInt[i] = taskCountList.get(i).getTaskCountDouble();

                if (taskFinishList.get(i).getFinishTime().equals(taskCountList.get(i).getCreateTime())){
                    if (i<n-1){
                        secondInt[i]=min-taskFinishList.get(i).getTaskCountInt();
                        min=secondInt[i]+taskCountList.get(i+1).getTaskCountInt();
                    }else{
                        secondInt[i]=min-taskFinishList.get(i).getTaskCountInt();
                    }
                }
            }
        }

        statisticsBurnout.setCumulativeTask(firstArray);
        statisticsBurnout.setCompletionTask(secondArray);

        statisticsBurnout.setTrueTask(secondInt);
        statisticsBurnout.setIdealTask(everyDateInt);
        statisticsBurnout.setEveryDate(everyDateName);

        return statisticsBurnout;
    }



   //任务燃尽图
    private StatisticsBurnout getBurnout(List<StatisticsResultVO> taskCountList, List<StatisticsResultVO> taskFinishList) {
        int n=taskCountList.size();
        StatisticsBurnout statisticsBurnout = new StatisticsBurnout();
        String[] everyDateName = new String[n];
        Double[] everyDateInt = new Double[n];
        Integer[] secondInt = new Integer[n];
        int min=taskCountList.get(0).getTaskCountInt();

        for (int i=0;i<n;i++){
            everyDateName[i]=taskCountList.get(i).getCreateTime();
            everyDateInt[i] = taskCountList.get(i).getTaskCountDouble();
            if (taskFinishList.get(i).getFinishTime().equals(taskCountList.get(i).getCreateTime())){
                if (i<n-1){
                    secondInt[i]=min-taskFinishList.get(i).getTaskCountInt();
                    min=secondInt[i]+taskCountList.get(i+1).getTaskCountInt();
                }else{
                    secondInt[i]=min-taskFinishList.get(i).getTaskCountInt();
                }
            }
        }
        statisticsBurnout.setTrueTask(secondInt);
        statisticsBurnout.setIdealTask(everyDateInt);
        statisticsBurnout.setEveryDate(everyDateName);
        return statisticsBurnout;
    }



    private StatisticsBurnout getCumulative(List<StatisticsResultVO> taskCountList, List<StatisticsResultVO> taskFinishList, String projectId, StaticDto sto) {


        sto =  resultStatic(sto);
        int n=taskCountList.size();
        Long currentDate=System.currentTimeMillis()/1000;

        StatisticsBurnout statisticsBurnout = new StatisticsBurnout();

        int finishCount=this.statisticsMapper.taskFinishOfSevenDayAgo(projectId, currentDate,null);

        Integer[] firstArray = new Integer[n];
        Integer[] secondArray = new Integer[n];
        String[] everyDateName = new String[n];

        for (int i=0;i<n;i++){
            firstArray[i] = taskCountList.get(i).getTaskCountAdd();
            secondArray[i] = taskFinishList.get(i).getTaskCountInt()+finishCount;
            finishCount=secondArray[i];
            everyDateName[i]=taskCountList.get(i).getCreateTime();
        }

        statisticsBurnout.setCumulativeTask(firstArray);
        statisticsBurnout.setCompletionTask(secondArray);
        statisticsBurnout.setEveryDate(everyDateName);

        return  statisticsBurnout;
    }

        private  List<StatisticsPie> getPieSource(List<StatisticsPie> statisticsPies){
            float noUserInt=0F;
            for (int i=0;i<statisticsPies.size();i++) {
                if (statisticsPies.get(i).getName().equals("待认领")){
                    noUserInt+=statisticsPies.get(i).getY();
                    statisticsPies.remove(i);
                    i--;

                }
            }
            if (noUserInt!=0f){
                StatisticsPie statisticsPie = new StatisticsPie();
                statisticsPie.setName("待认领");
                statisticsPie.setY(noUserInt);
                statisticsPies.add(statisticsPie);
                return statisticsPies;
            }
            return statisticsPies;
        }


}
