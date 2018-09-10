package com.art1001.supply.service.task.impl;

import com.art1001.supply.entity.statistics.StatisticsDTO;
import com.art1001.supply.entity.statistics.StatisticsResultVO;
import com.art1001.supply.entity.statistics.StaticticsVO;
import com.art1001.supply.mapper.task.TaskInfoMapper;
import com.art1001.supply.service.task.TaskInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模块
 *
 * @author lujing
 * @date 2018-09-05 15:34
 */
@Service
public class TaskInfoServiceImpl implements TaskInfoService {

    /**
     * 统计页面数据详情
     */
    @Resource
     private TaskInfoMapper taskInfoMapper;

     private static Map<String, Object> map = null;

     /**
      * 查询未完成和已完成时的传值
      */
     private static  final String  UNFINISH_TASK_CASE="未完成";

     private static  final String  FINISH_TASK_CASE="完成";



    @Override
    public Map selectTask(StatisticsDTO statisticsDTO, String chartId) {
        map = new HashMap<>(16);
        /*
         *查询统计页面总量概览的chart参数
         * 1 未完成 2 已完成 3 总量 4 今日到期 5 已逾期 6 待认领 7 按时完成 8 逾期完成
         * TASKONE 任务按完成情况分布 TASKTWO 任务按任务分组分布 TASKTHREE 任务按执行者分布 TASKFOUR 任务按优先级分布
         * TASKFIVE  期间完成的任务   TASKSIX 期间未完成的任务   TASKSERVEN   期间逾期的任务
         */
        if (chartId != null && !"".equals(chartId)) {
            if (StaticticsVO.HANGINTHEAIR.equals(chartId)) { return this.selectUnfinishedTask(statisticsDTO); }
            else if (StaticticsVO.COMPLETED.equals(chartId)) { return this.selectFinishedTask(statisticsDTO); }
            else if (StaticticsVO.TASKTOTALCOUNT.equals(chartId)) { return this.selectTotalTask(); }
            else if (StaticticsVO.MATURINGTODAY.equals(chartId)) { return this.selectExpireTask(statisticsDTO); }
            else if (StaticticsVO.BEOVERDUE.equals(chartId)) { return this.selectOverdueTask(statisticsDTO); }
            else if (StaticticsVO.TOBECLAIMED.equals(chartId)) { return this.selectWaitClaimTask(statisticsDTO); }
            else if (StaticticsVO.FINISHONTIME.equals(chartId)) { return this.selectPunctualityTask(statisticsDTO); }
            else if (StaticticsVO.OVERDUECOMPLETION.equals(chartId)) { return this.selectExpiredToCompleteTask(statisticsDTO); }
            else if (StaticticsVO.TASKONE.equals(chartId)) { return this.selectCompletionTask(statisticsDTO); }
            else if (StaticticsVO.TASKTWO.equals(chartId)) { return this.selectGroupByTask(statisticsDTO); }
            else if (StaticticsVO.TASKTHREE.equals(chartId)) { return this.selectExcutorTask(statisticsDTO); }
            else if (StaticticsVO.TASKFOUR.equals(chartId)) { return this.selectPriorityTask(statisticsDTO); }
            else if (StaticticsVO.TASKFIVE.equals(chartId)) { return this.selectTimeFinish(statisticsDTO); }
            else if (StaticticsVO.TASKSIX.equals(chartId)) { return this.selectTimeUnfinish(statisticsDTO); }
            else if (StaticticsVO.TASKSERVEN.equals(chartId)) { return this.selectTimeOverdue(statisticsDTO); }
            else if (StaticticsVO.TASKEIGHT.equals(chartId)) { return this.selectUpdateTaskTime(statisticsDTO); }
            else if (StaticticsVO.TASKNINE.equals(chartId)) { return this.selectMoreParticipation(statisticsDTO); }
            else if (StaticticsVO.TASKTEN.equals(chartId)) { return this.selectEndTimeTaskFinish(statisticsDTO); }
            else if (StaticticsVO.TASK1.equals(chartId)) { return this.selectTaskByExcutor(statisticsDTO); }
            else if (StaticticsVO.TASK2.equals(chartId)) { return this.selectTaskByEndTime(statisticsDTO); }
            else if (StaticticsVO.TASK3.equals(chartId)) { return this.selectProjectProgress(statisticsDTO); }
            else if (StaticticsVO.TASK4.equals(chartId)) { return this.selectDayFinish(statisticsDTO); }
            else if (StaticticsVO.TASK5.equals(chartId)) { return this.selectDayFinishByAVG(statisticsDTO); }
            else if (StaticticsVO.TASK6.equals(chartId)) { return this.selectTaskBurnOut(statisticsDTO); }
            else if (StaticticsVO.TASK7.equals(chartId)) { return this.selectTaskDifSuccess(statisticsDTO); }
        } else {
            System.out.println(">>>>>>>>>chartId=" + chartId + "statisticsDTO=" + statisticsDTO + ">>>>>>>");
            throw new NullPointerException();
        }
        return map;
    }

    /**
     * 未完成数据
     */
    private Map selectUnfinishedTask(StatisticsDTO statisticsDTO) {
        try {
            // 总任务数
            int count=this.taskInfoMapper.selectTaskCount();
            //查询未完成数据
            List<StatisticsResultVO> statisticsResultVOList = this.taskInfoMapper.selectUnfinishTask(statisticsDTO,UNFINISH_TASK_CASE);
            map.put("未完成百分比", numChange((double)statisticsResultVOList.size()/count));
            map.put("未完成", statisticsResultVOList.size());
            map.put("详情表", statisticsResultVOList);
        } catch (Exception e) {
            System.out.println("---未完成报错---");
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 已完成数据
     */
    private Map selectFinishedTask(StatisticsDTO statisticsDTO) {
        try {
            //总任务数
            int count = this.taskInfoMapper.selectTaskCount();
            //查询已完成任务
            List<StatisticsResultVO> statisticsResultVOList = this.taskInfoMapper.selectFinishTask(statisticsDTO,FINISH_TASK_CASE);
            map.put("已完成百分比",numChange((double)statisticsResultVOList.size()/count));
            map.put("已完成", statisticsResultVOList.size());
            map.put("详情表", statisticsResultVOList);
        } catch (Exception e) {
            System.out.println("---已完成报错---");
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 总数据
     */
    private Map selectTotalTask() {
        try {
            int count = this.taskInfoMapper.selectTaskCount();
            map.put("任务总量", count);
        } catch (Exception e) {
            System.out.println("任务总量报错");
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 今日到期
     */
    private Map selectExpireTask(StatisticsDTO statisticsDTO) {
        try {
            //总任务数
            int count = this.taskInfoMapper.selectTaskCount();
            //查询今日到期的数据
            List<StatisticsResultVO> statisticsResultVOList = this.taskInfoMapper.selectExpireTask(statisticsDTO);
            map.put("已到期百分比",numChange((double) statisticsResultVOList.size() / count));
            map.put("已到期", statisticsResultVOList.size() / count);
            map.put("详情表", statisticsResultVOList);
        } catch (Exception e) {
            System.out.println("今日到期报错");
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 已逾期
     */
    private Map selectOverdueTask(StatisticsDTO statisticsDTO) {
        try {
            //总任务数
            int count = this.taskInfoMapper.selectTaskCount();
            //已逾期数据
            List<StatisticsResultVO>  statisticsResultVOList = this.taskInfoMapper.selectOverdueTask(statisticsDTO);
            map.put("已逾期百分比", numChange((double) statisticsResultVOList.size() / count));
            map.put("已逾期", statisticsResultVOList.size());
            map.put("详情表", statisticsResultVOList);
        } catch (Exception e) {
            System.out.println("已逾期报错");
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 待认领
     */
    private Map selectWaitClaimTask(StatisticsDTO statisticsDTO) {
        try {
            //总任务数
            int count = this.taskInfoMapper.selectTaskCount();
            //待认领数据
            List<StatisticsResultVO> statisticsResultVOList =this.taskInfoMapper.selectWaitClaimTask(statisticsDTO);
            map.put("待认领百分比", numChange((double)statisticsResultVOList.size() / count));
            map.put("待认领", statisticsResultVOList.size() / count);
            map.put("详情表", statisticsResultVOList);
        } catch (Exception e) {
            System.out.println("待认领报错");
            map = null;
            e.printStackTrace();
        }
        return map;

    }

    /**
     * 按时完成数据
     */
    private Map selectPunctualityTask(StatisticsDTO statisticsDTO) {
        map = new HashMap<>(16);
        try {
            //总任务数
            int count = this.taskInfoMapper.selectTaskCount();
            //按时完成数据
            List<StatisticsResultVO> statisticsResultVOList = this.taskInfoMapper.selectPunctualityTask(statisticsDTO);
            map.put("按时完成百分比",numChange((double)statisticsResultVOList.size()/count));
            map.put("按时完成", statisticsResultVOList.size());
            map.put("详情表", statisticsResultVOList);
        } catch (Exception e) {
            System.out.println("按时完成报错");
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 逾期完成数据
     */
    private Map selectExpiredToCompleteTask(StatisticsDTO statisticsDTO) {
        try {
            //总任务数
            int count =  this.taskInfoMapper.selectTaskCount();
            List<StatisticsResultVO> statisticsResultVOList = this.taskInfoMapper.selectExpiredToCompleteTask(statisticsDTO);
            map.put("逾期完成百分比", numChange((double)statisticsResultVOList.size()/count));
            map.put("逾期完成", statisticsResultVOList.size());
            map.put("详情表", statisticsResultVOList);
        } catch (Exception e) {
            System.out.println("逾期完成报错");
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 任务按完成情况分布
     */
    private Map selectCompletionTask(StatisticsDTO statisticsDTO) {
        try {
            //总任务数
            int count = this.taskInfoMapper.selectTaskCount();
            //未完成任务
            int unfinishCount = this.taskInfoMapper.selectUnfinishCount(statisticsDTO,UNFINISH_TASK_CASE);
            //已完成任务
            int finishCount = this.taskInfoMapper.selectFinishCount(statisticsDTO,FINISH_TASK_CASE);
            List<StatisticsResultVO> statisticsResultVOList = new ArrayList<>();
            StatisticsResultVO vO1=new StatisticsResultVO();
            vO1.setFinishTaskNum(unfinishCount);
            vO1.setTaskCase(UNFINISH_TASK_CASE);
            StatisticsResultVO vO2=new StatisticsResultVO();
            vO2.setFinishTaskNum(finishCount);
            vO2.setTaskCase(FINISH_TASK_CASE);
            statisticsResultVOList.add(vO1);
            statisticsResultVOList.add(vO2);
            map.put("未完成百分比", numChange((double)unfinishCount / count));
            map.put("已完成百分比", numChange((double)finishCount / count));
            map.put("详情表", statisticsResultVOList);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 任务按任务分组分布
     */
    private Map selectGroupByTask(StatisticsDTO statisticsDTO) {
        try {
            List<StatisticsResultVO> statisticsResultVOList = this.taskInfoMapper.selectGroupByTask(statisticsDTO);
            //创建String对象 拼接前端需要的分组和任务数量数据
            StringBuilder groupName = new StringBuilder();
            StringBuilder unFinishNum = new StringBuilder();
            StringBuilder finishNum = new StringBuilder();
            //循环获取已完成和未完成 组名和任务数
            if (statisticsResultVOList!=null && statisticsResultVOList.size()>0){
                    for (StatisticsResultVO t : statisticsResultVOList) {
                        groupName.append(t.getTaskGroup()).append(",");
                        unFinishNum.append(t.getUnfinishTaskNum()).append(",");
                        finishNum.append(t.getFinishTaskNum()).append(",");
                    }
                    if (groupName.length() > 0 && unFinishNum.length() > 0 && finishNum.length() > 0){
                        map.put("任务分组", percentChange(groupName.toString()));
                        map.put("未完成", percentChange(unFinishNum.toString()));
                        map.put("已完成", percentChange(finishNum.toString()));
                    }

            }
            map.put("详情表", statisticsResultVOList);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return  map;
    }


    /**
     * 任务按优先级分布
     */
    private Map selectPriorityTask(StatisticsDTO statisticsDTO) {
        map = new HashMap<>(16);
        try {
            List<StatisticsResultVO> statisticsResultVOList = new ArrayList<>();
            StatisticsResultVO to1 = new StatisticsResultVO();
            to1.setTaskPrecedence("十万火急");
            to1.setTaskCountInt(30);
            StatisticsResultVO to2 = new StatisticsResultVO();
            to2.setTaskPrecedence("紧急");
            to2.setTaskCountInt(12);
            StatisticsResultVO to3 = new StatisticsResultVO();
            to3.setTaskPrecedence("普通");
            to3.setTaskCountInt(9);
            statisticsResultVOList.add(to1);
            statisticsResultVOList.add(to2);
            statisticsResultVOList.add(to3);
            List<StatisticsResultVO> taskDetaileList = new ArrayList<>();
            StatisticsResultVO to4 = new StatisticsResultVO();
            to4.setTaskName("零零壹");
            to4.setTaskPrecedence("十万火急");
            to4.setTaskCase("未完成");
            to4.setExecutor("韩梅");
            StatisticsResultVO to5 = new StatisticsResultVO();
            to5.setTaskName("零零贰");
            to5.setTaskPrecedence("普通");
            to5.setTaskCase("已完成");
            to5.setExecutor("李雷");
            StatisticsResultVO to6 = new StatisticsResultVO();
            to6.setTaskName("零零叁");
            to6.setTaskPrecedence("十万火急");
            to6.setTaskCase("未完成");
            to6.setExecutor("韩梅");
            taskDetaileList.add(to4);
            taskDetaileList.add(to5);
            taskDetaileList.add(to6);
            double count=0 ;
            for (StatisticsResultVO t: statisticsResultVOList) {
                count+=t.getTaskCountInt();
            }

            for (StatisticsResultVO t: statisticsResultVOList) {
                map.put(t.getTaskPrecedence(),this.numChange(t.getTaskCountInt()/count));
            }
            map.put("详情表", statisticsResultVOList);
            map.put("任务明细表", taskDetaileList);
        } catch (Exception e) {
            System.out.println("按完成情况分布报错");
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 任务按执行者分布
     */
    private Map selectExcutorTask(StatisticsDTO statisticsDTO) {
        map = new HashMap<>(16);
        try {
            List<StatisticsResultVO> taskDistributionList = new ArrayList<>();
            StatisticsResultVO to1 = new StatisticsResultVO();
            to1.setExecutor("李雷");
            to1.setTaskCountInt(10);
            StatisticsResultVO to2 = new StatisticsResultVO();
            to2.setExecutor("韩梅");
            to2.setTaskCountInt(5);
            taskDistributionList.add(to1);
            taskDistributionList.add(to2);
            double count=0 ;
            for (StatisticsResultVO t:taskDistributionList) {
                count+=t.getTaskCountInt();
            }
            for (StatisticsResultVO t:taskDistributionList) {
                 map.put(t.getExecutor(),numChange(t.getTaskCountInt()/count));
            }
            map.put("详情表", taskDistributionList);
        } catch (Exception e) {
            System.out.println("按完成情况分布报错");
            map = null;
            e.printStackTrace();
        }
        return map;
    }





    private Map selectTimeOverdue(StatisticsDTO statisticsDTO) {
        map = new HashMap<>(16);
        try {
//        this.taskInfoMapper.selectUnfinishedTask(statisticsDTO);
            List<StatisticsResultVO> statisticsResultVOList = new ArrayList<>();
            StatisticsResultVO to1 = new StatisticsResultVO();
            to1.setExecutor("吴XX");
            to1.setTaskCountInt(10);
            StatisticsResultVO to2 = new StatisticsResultVO();
            to2.setExecutor("郑VV");
            to2.setTaskCountInt(22);
            StatisticsResultVO to10 = new StatisticsResultVO();
            to10.setExecutor(null);
            to10.setTaskCountInt(15);
            statisticsResultVOList.add(to1);
            statisticsResultVOList.add(to2);
            statisticsResultVOList.add(to10);
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            for (StatisticsResultVO t : statisticsResultVOList) {
                sb1.append(t.getExecutor()== null?"待认领":t.getExecutor()).append(",");
                sb2.append(t.getTaskCountInt()).append(",");
            }
            if (sb1.length() > 0 && sb2.length() > 0){
                map.put("执行者", percentChange(sb1.toString()));
                map.put("任务数", percentChange(sb2.toString()));
            }
            //详情表信息
            List<StatisticsResultVO> statisticsResultVOList1 = new ArrayList<>();
            StatisticsResultVO to3 = new StatisticsResultVO();
            to3.setTaskName("零零1");
            to3.setEndTime("2018-08-21");
            to3.setExecutor("赑屃先生");
            to3.setTaskGroup("任务19");
            to3.setListView("分组8");
            StatisticsResultVO to4 = new StatisticsResultVO();
            to4.setTaskName("零零2");
            to4.setEndTime("2018-09-11");
            to4.setExecutor("饕餮先生");
            to4.setTaskGroup("任务15");
            to4.setListView("分组5");
            statisticsResultVOList1.add(to3);
            statisticsResultVOList1.add(to4);

            map.put("详情表", statisticsResultVOList1);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    private Map selectTimeUnfinish(StatisticsDTO statisticsDTO) {
        map = new HashMap<>(16);
        try {
            List<StatisticsResultVO> statisticsResultVOList = new ArrayList<>();
            StatisticsResultVO to1 = new StatisticsResultVO();
            to1.setExecutor("赵XX");
            to1.setTaskCountInt(20);
            StatisticsResultVO to2 = new StatisticsResultVO();
            to2.setExecutor("李VV");
            to2.setTaskCountInt(30);
            StatisticsResultVO to10 = new StatisticsResultVO();
            to10.setExecutor(null);
            to10.setTaskCountInt(5);
            statisticsResultVOList.add(to1);
            statisticsResultVOList.add(to2);
            statisticsResultVOList.add(to10);
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            for (StatisticsResultVO t : statisticsResultVOList) {
                sb1.append(t.getExecutor()== null?"待认领":t.getExecutor()).append(",");
                sb2.append(t.getTaskCountInt()).append(",");
            }
            if (sb1.length() > 0 && sb2.length() > 0){
                map.put("执行者", percentChange(sb1.toString()));
                map.put("任务数", percentChange(sb2.toString()));
            }
            //详情表信息
            List<StatisticsResultVO> statisticsResultVOList1 = new ArrayList<>();
            StatisticsResultVO to3 = new StatisticsResultVO();
            to3.setTaskName("零零妖");
            to3.setEndTime("2018-08-21");
            to3.setExecutor("楚先生");
            to3.setTaskGroup("任务19");
            to3.setListView("分组8");
            StatisticsResultVO to4 = new StatisticsResultVO();
            to4.setTaskName("零零红中");
            to4.setEndTime("2018-09-11");
            to4.setExecutor("魏先生");
            to4.setTaskGroup("任务15");
            to4.setListView("分组5");
            statisticsResultVOList1.add(to3);
            statisticsResultVOList1.add(to4);

            map.put("详情表", statisticsResultVOList1);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    private Map selectTimeFinish(StatisticsDTO statisticsDTO) {
        map = new HashMap<>(16);
        try {
            List<StatisticsResultVO> statisticsResultVOList = new ArrayList<>();
            StatisticsResultVO to1 = new StatisticsResultVO();
            to1.setExecutor("张XX");
            to1.setTaskCountInt(10);
            StatisticsResultVO to2 = new StatisticsResultVO();
            to2.setExecutor("李XX");
            to2.setTaskCountInt(22);
            statisticsResultVOList.add(to1);
            statisticsResultVOList.add(to2);
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            for (StatisticsResultVO t : statisticsResultVOList) {
                sb1.append(t.getExecutor()).append(",");
                sb2.append(t.getTaskCountInt()).append(",");
            }
            if (sb1.length() > 0 && sb2.length() > 0){
                map.put("执行者", percentChange(sb1.toString()));
                map.put("任务数", percentChange(sb2.toString()));
            }
            //详情表信息
            List<StatisticsResultVO> statisticsResultVOList1 = new ArrayList<>();
            StatisticsResultVO to3 = new StatisticsResultVO();
            to3.setTaskName("零零发");
            to3.setFinishTime("2018-08-31");
            to3.setExecutor("冯先生");
            to3.setTaskGroup("任务10");
            to3.setListView("分组1");
            StatisticsResultVO to4 = new StatisticsResultVO();
            to4.setTaskName("零零白板");
            to4.setFinishTime("2018-09-01");
            to4.setExecutor("陈先生");
            to4.setTaskGroup("任务11");
            to4.setListView("分组2");
            statisticsResultVOList1.add(to3);
            statisticsResultVOList1.add(to4);

            map.put("详情表", statisticsResultVOList1);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }
    /**
     *高频参与任务
     */
    private Map selectMoreParticipation(StatisticsDTO statisticsDTO) {
        map = new HashMap<>(16);
        try {
            List<StatisticsResultVO> statisticsResultVOList = new ArrayList<>();
            StatisticsResultVO to1 = new StatisticsResultVO();
            to1.setTaskName("78910J");
            to1.setDynamicNum(11);
            to1.setEndTime("2018-08-21 18:40");
            to1.setExecutor("Z先生");
            to1.setTaskGroup("分组1");
            to1.setListView("任务8");
            StatisticsResultVO to2 = new StatisticsResultVO();
            to2.setTaskName("12345");
            to2.setDynamicNum(15);
            to2.setEndTime("2018-08-22 18:40");
            to2.setExecutor("F先生");
            to2.setTaskGroup("分组2");
            to2.setListView("任务5");
            statisticsResultVOList.add(to1);
            statisticsResultVOList.add(to2);

          String[] excutor={"X先生","Y先生","Z先生"};
          int[] excutorNum={12,10,7};

            map.put("执行者",excutor);
            map.put("任务数",excutorNum);
            map.put("详情表", statisticsResultVOList);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }
    /**
     * 截至时间完成
     */
    private Map selectEndTimeTaskFinish(StatisticsDTO statisticsDTO) {
        map = new HashMap<>(16);
        try {
            //总任务数
            double count = 62D;
            //未完成数
            double unFinish = 49D;
            //完成数
            double finish = 13D;
            map.put("未完成百分比", numChange(unFinish/count));
            map.put("已完成百分比", numChange(finish/count));
//        this.taskInfoMapper.selectUnfinishedTask(statisticsDTO);
            List<StatisticsResultVO> taskDistributionList = new ArrayList<>();
            StatisticsResultVO to1 = new StatisticsResultVO();

            to1.setUnfinishTaskNum(12);
            to1.setFinishTaskNum(4);
            StatisticsResultVO to2 = new StatisticsResultVO();
            to2.setListView("任务2");
            to2.setUnfinishTaskNum(22);
            to2.setFinishTaskNum(8);
            taskDistributionList.add(to1);
            taskDistributionList.add(to2);
            //创建String对象 拼接前端需要的分组和任务数量数据
            StringBuilder groupName = new StringBuilder();
            StringBuilder unFinishNum = new StringBuilder();
            StringBuilder finishNum = new StringBuilder();
            //循环获取已完成和未完成 组名和任务数
            for (StatisticsResultVO t : taskDistributionList) {
                groupName.append(t.getListView()).append(",");
                unFinishNum.append(t.getUnfinishTaskNum()).append(",");
                finishNum.append(t.getFinishTaskNum()).append(",");
            }
            if (groupName.length() > 0 && unFinishNum.length() > 0 && finishNum.length() > 0){
                map.put("任务分组", percentChange(groupName.toString()));
                map.put("未完成", percentChange(unFinishNum.toString()));
                map.put("已完成", percentChange(finishNum.toString()));
            }
            map.put("详情表", taskDistributionList);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return  map;
    }

    /**
     * 更新截止时间
     */
    private Map selectUpdateTaskTime(StatisticsDTO statisticsDTO) {
        map = new HashMap<>(16);
        try {

            //详情表信息
            List<StatisticsResultVO> statisticsResultVOList1 = new ArrayList<>();
            StatisticsResultVO to3 = new StatisticsResultVO();
            to3.setTaskName("003");
            to3.setCreateTime("2018-08-20 15:30");
            to3.setEndTime("2018-08-21  18:40");
            to3.setExecutor("X先生");
            to3.setTaskGroup("任务8");
            to3.setListView("分组1");
            StatisticsResultVO to4 = new StatisticsResultVO();
            to4.setTaskName("002");
            to4.setCreateTime("2018-09-01 15:30");
            to4.setEndTime("2018-09-10 17:40");
            to4.setExecutor("Y先生");
            to4.setTaskGroup("任务5");
            to4.setListView("分组6");
            statisticsResultVOList1.add(to3);
            statisticsResultVOList1.add(to4);

            //chart数据
            String[] excutor={"W先生","G先生","L先生"};
            int[] excutorNum={11,10,7};

            map.put("执行者",excutor);
            map.put("任务数",excutorNum);
            map.put("详情表", statisticsResultVOList1);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 期间截止任务分成员完成情况
     */
    private Map selectTaskByExcutor(StatisticsDTO statisticsDTO) {
        map =new HashMap<>();
        try {
            List<StatisticsResultVO> statisticsResultVOList = new ArrayList<>();
            StatisticsResultVO to1 = new StatisticsResultVO();
            to1.setEndTime("2018-08-21");
            to1.setTaskName("施工计划");
            to1.setTaskCase("未完成");
            to1.setFinishTime("2018-08-21 18:00");
            to1.setExecutor("王先生");
            StatisticsResultVO to2 = new StatisticsResultVO();
            to2.setEndTime("2018-08-22");
            to2.setTaskName("深化设计");
            to2.setFinishTime("2018-08-22 18:00");
            to2.setExecutor("玉先生");
            statisticsResultVOList.add(to1);
            statisticsResultVOList.add(to2);

            String  excutor="X,Y,Z,W";
            String  unFinish="1,52,12,4";
            String  finish="10,5,2,44";

            map.put("执行者",excutor);
            map.put("未完成任务",unFinish);
            map.put("已完成任务",finish);
            map.put("详情表", statisticsResultVOList);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    private Map selectTaskByEndTime(StatisticsDTO statisticsDTO) {
        map =new HashMap<>();
        try {
            List<StatisticsResultVO> statisticsResultVOList = new ArrayList<>();
            StatisticsResultVO to1 = new StatisticsResultVO();
            to1.setEndTime("2018-08-21  10:00");
            to1.setTaskName("V计划");
            to1.setTaskCase("未完成");
            to1.setFinishTime("2018-08-21 18:00");
            to1.setExecutor("王先生");
            StatisticsResultVO to2 = new StatisticsResultVO();
            to2.setEndTime("2018-08-22 10:00");
            to2.setTaskName("Z设计");
            to2.setFinishTime("2018-08-22 18:00");
            to2.setExecutor("玉先生");
            statisticsResultVOList.add(to1);
            statisticsResultVOList.add(to2);

            String  excutor="8-31,9-01,9-02,9-03,9-04,9-05";
            String  unFinish="1,2,3,4,5,6,7";
            String  finish="8,9,10,15,15";

            map.put("日期",excutor);
            map.put("未完成任务数",unFinish);
            map.put("已完成任务数",finish);
            map.put("详情表", statisticsResultVOList);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    /**
     *项目进展走势
     */
    private Map selectProjectProgress(StatisticsDTO statisticsDTO) {
        map=new HashMap<>(16);
        try {
            List<StatisticsResultVO> statisticsResultVOList = new ArrayList<>();
            StatisticsResultVO to1 = new StatisticsResultVO();
            to1.setCreateTime("2018-08-21");
            to1.setTaskName("RZ计划");
            to1.setExecutor("H先生");
            StatisticsResultVO to2 = new StatisticsResultVO();
            to2.setCreateTime("2018-08-22");
            to2.setTaskName("XC设计");
            to2.setExecutor("A先生");
            statisticsResultVOList.add(to1);
            statisticsResultVOList.add(to2);

            String  excutor="8-31,9-01,9-02,9-03,9-04,9-05";
            String  finish="1,2,3,4,5,6,7";
            String  unFinish="8,9,10,15,15";

            map.put("日期",excutor);
            map.put("累计总任务数",unFinish);
            map.put("累计完成任务数",finish);
            map.put("详情表", statisticsResultVOList);
        } catch (Exception e) {
            map=null;
            e.printStackTrace();
        }
        return  map;
    }

    /**
     * 每日完成任务量
     */
    private Map selectDayFinish(StatisticsDTO statisticsDTO) {
        map = new HashMap<>(16);
        try {
//        this.taskInfoMapper.selectUnfinishedTask(statisticsDTO);
            List<StatisticsResultVO> statisticsResultVOList = new ArrayList<>();
            StatisticsResultVO to1 = new StatisticsResultVO();
            to1.setFinishTime("2018-08-21");
            to1.setTaskName("施工计划");
            to1.setExecutor("张先生");
            to1.setTaskDayNum("1");
            StatisticsResultVO to2 = new StatisticsResultVO();
            to2.setEndTime("2018-08-22");
            to2.setTaskName("深化设计");
            to2.setExecutor("王先生");
            to2.setTaskGroup("任务Two");
            to2.setListView("合同签订");
            to2.setTaskDayNum("2");
            statisticsResultVOList.add(to1);
            statisticsResultVOList.add(to2);

            String  excutor="8-31,9-01,9-02,9-03,9-04,9-05";
            String  finish="8,9,10,15,15";

            map.put("日期",excutor);
            map.put("每日完成任务数",finish);
            map.put("详情表", statisticsResultVOList);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    /**
     *任务燃尽图
     */
    private Map selectTaskBurnOut(StatisticsDTO statisticsDTO) {
        map = new HashMap<>(16);
        try {
//        this.taskInfoMapper.selectUnfinishedTask(statisticsDTO);
            List<StatisticsResultVO> statisticsResultVOList = new ArrayList<>();
            StatisticsResultVO to1 = new StatisticsResultVO();
            to1.setCreateTime("2018-08-21 18:00");
            to1.setTaskName("施工计划");
            to1.setChangeType("新增");
            to1.setTaskCountString("+1");
            StatisticsResultVO to2 = new StatisticsResultVO();
            to2.setCreateTime("2018-08-22  17:00");
            to2.setTaskName("深化设计");
            to2.setChangeType("完成");
            to2.setTaskCountString("-1");
            statisticsResultVOList.add(to1);
            statisticsResultVOList.add(to2);

            String  dayNum="8-31,9-01,9-02,9-03,9-04,9-05";
            String  reality="8.0,2.9,4.0,5.5,7.5,8.7";
            String  ideality="1.0,0.9,2.0,1.5,1.5,1.7";

            map.put("日期",dayNum);
            map.put("实际剩余任务数",ideality);
            map.put("理想剩余任务数",reality);
            map.put("详情表", statisticsResultVOList);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    /**
     *不同任务分组已完成任务量
     */
    private Map selectTaskDifSuccess(StatisticsDTO statisticsDTO) {
        map = new HashMap<>(16);
        try {
//        this.taskInfoMapper.selectUnfinishedTask(statisticsDTO);
            List<StatisticsResultVO> statisticsResultVOList = new ArrayList<>();
            StatisticsResultVO to1 = new StatisticsResultVO();
            to1.setTaskGroup("任务1");
            to1.setTaskCountInt(15);
            StatisticsResultVO to2 = new StatisticsResultVO();
            to2.setTaskGroup("任务2");
            to2.setTaskCountInt(18);
            statisticsResultVOList.add(to1);
            statisticsResultVOList.add(to2);

            String taskName="任务1,任务2";
            String  TaskFinishNum="15,18";

            map.put("任务分组",taskName);
            map.put("已完成任务数",TaskFinishNum);
            map.put("详情表", statisticsResultVOList);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }


    /**
     * 去除拼接字符串最后的逗号
     */
    private String  percentChange(String num){
        return num.substring(0, num.length() - 1);
    }
    /**
     * 将小数转换为百分比
     */
    private String  numChange(double num){
        //获取格式化对象
        NumberFormat nt = NumberFormat.getPercentInstance();
        //设置百分数精确度2即保留两位小数
        nt.setMinimumFractionDigits(2);
        String format = nt.format(num);
        return format;

    }

    /**
     *每日完成任务的平均完成天数
     */
    private Map selectDayFinishByAVG(StatisticsDTO statisticsDTO) {
        map = new HashMap<>(16);
        try {
            List<StatisticsResultVO> statisticsResultVOList = new ArrayList<>();
            StatisticsResultVO to1 = new StatisticsResultVO();
            to1.setFinishTime("2018-08-21  15:00");
            to1.setTaskName("施工计划");
            to1.setExecutor("Z先生");
            to1.setTaskDayNum("4");
            StatisticsResultVO to2 = new StatisticsResultVO();
            to2.setEndTime("2018-08-22  17:00");
            to2.setTaskName("深化设计");
            to2.setExecutor("W先生");
            to2.setTaskDayNum("8");
            statisticsResultVOList.add(to1);
            statisticsResultVOList.add(to2);

            String  excutor="8-31,9-01,9-02,9-03,9-04,9-05";
            String  finish="1.0,0.9,2.0,1.5,1.5,1.7";

            map.put("日期",excutor);
            map.put("平均完成天数",finish);
            map.put("详情表", statisticsResultVOList);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }

}
