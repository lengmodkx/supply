package com.art1001.supply.service.task.impl;

import com.art1001.supply.entity.statistics.StaticticsVO;
import com.art1001.supply.entity.statistics.TaskCondition;
import com.art1001.supply.entity.statistics.TaskDistribution;
import com.art1001.supply.entity.statistics.TotalOverView;
import com.art1001.supply.service.task.TaskInfoService;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.stereotype.Service;

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
    // private TaskInfoMapper taskInfoMapper;

    private static Map<String, Object> map = null;



    @Override
    public Map selectTask(TaskCondition taskCondition, String chartId) {
        /*
         *获取任务总量
         */
        //int count=62;


        /*
         *查询统计页面总量概览的chart参数
         * 1 未完成 2 已完成 3 总量 4 今日到期 5 已逾期 6 待认领 7 按时完成 8 逾期完成
         */
        /*
         *查询任务分布的chart数据
         * TASKONE 任务按完成情况分布 TASKTWO 任务按任务分组分布 TASKTHREE 任务按执行者分布 TASKFOUR 任务按优先级分布
         * TASKFIVE  期间完成的任务   TASKSIX 期间未完成的任务   TASKSERVEN   期间逾期的任务
         */
        if (chartId != null && !"".equals(chartId)) {
            if (StaticticsVO.HANGINTHEAIR.equals(chartId)) {
                return this.selectUnfinishedTask(taskCondition);
            }
            else if (StaticticsVO.COMPLETED.equals(chartId)) {
                return this.selectFinishedTask(taskCondition);
            }
            else if (StaticticsVO.TASKTOTALCOUNT.equals(chartId)) {
                return this.selectTotalTask(taskCondition);
            }
            else if (StaticticsVO.MATURINGTODAY.equals(chartId)) {
                return this.selectExpireTask(taskCondition);
            }
            else if (StaticticsVO.BEOVERDUE.equals(chartId)) {
                return this.selectOverdueTask(taskCondition);
            }
            else if (StaticticsVO.TOBECLAIMED.equals(chartId)) {
                return this.selectWaitClaimTask(taskCondition);
            }
            else if (StaticticsVO.FINISHONTIME.equals(chartId)) {
                return this.selectPunctualityTask(taskCondition);
            }
            else if (StaticticsVO.OVERDUECOMPLETION.equals(chartId)) {
                return this.selectExpiredToCompleteTask(taskCondition);
            }
            else if (StaticticsVO.TASKONE.equals(chartId)) {
                return this.selectCompletionTask(taskCondition);
            }
            else if (StaticticsVO.TASKTWO.equals(chartId)) {
                return this.selectGroupByTask(taskCondition);
            }
            else if (StaticticsVO.TASKTHREE.equals(chartId)) {
                return this.selectExcutorTask(taskCondition);
            }
            else if (StaticticsVO.TASKFOUR.equals(chartId)) {
                return this.selectPriorityTask(taskCondition);
            }
            else if (StaticticsVO.TASKFIVE.equals(chartId)) {
                return this.selectTimeFinish(taskCondition);
            }
            else if (StaticticsVO.TASKSIX.equals(chartId)) {
                return this.selectTimeUnfinish(taskCondition);
            }
            else if (StaticticsVO.TASKSERVEN.equals(chartId)) {
                return this.selectTimeOverdue(taskCondition);
            }
            else if (StaticticsVO.TASKEIGHT.equals(chartId)) {
                return this.selectUpdateTaskTime(taskCondition);
            }
            else if (StaticticsVO.TASKNINE.equals(chartId)) {
                return this.selectMoreParticipation(taskCondition);
            }
            else if (StaticticsVO.TASKTEN.equals(chartId)) {
                return this.selectEndTimeTaskFinish(taskCondition);
            }
            else if (StaticticsVO.TASK1.equals(chartId)) {
                return this.selectTaskByExcutor(taskCondition);
            }
            else if (StaticticsVO.TASK2.equals(chartId)) {
                return this.selectTaskByEndTime(taskCondition);
            }
            else if (StaticticsVO.TASK3.equals(chartId)) {
                return this.selectProjectProgress(taskCondition);
            }
            else if (StaticticsVO.TASK4.equals(chartId)) {
                return this.selectDayFinish(taskCondition);
            }
            else if (StaticticsVO.TASK5.equals(chartId)) {
                return this.selectDayFinishByAVG(taskCondition);
            }
            else if (StaticticsVO.TASK6.equals(chartId)) {
                return this.selectTaskBurnOut(taskCondition);
            }
            else if (StaticticsVO.TASK7.equals(chartId)) {
                return this.selectTaskDifSuccess(taskCondition);
            }
        } else {
            System.out.println(">>>>>>>>>chartId=" + chartId + "taskCondition=" + taskCondition + ">>>>>>>");
            throw new NullPointerException();
        }

        return map;
    }

    /**
     *每日完成任务的平均完成天数
     */
    private Map selectDayFinishByAVG(TaskCondition taskCondition) {
        map = new HashMap<>(16);
        try {
//        this.taskInfoMapper.selectUnfinishedTask(taskCondition);
            List<TotalOverView> totalOverViewList = new ArrayList<>();
            TotalOverView to1 = new TotalOverView();
            to1.setSuccessTime("2018-08-21  15:00");
            to1.setTaskName("施工计划");
            to1.setExecutor("Z先生");
            to1.setTaskDayNum("4");
            TotalOverView to2 = new TotalOverView();
            to2.setEndTime("2018-08-22  17:00");
            to2.setTaskName("深化设计");
            to2.setExecutor("W先生");
            to2.setTaskDayNum("8");
            totalOverViewList.add(to1);
            totalOverViewList.add(to2);

            String  excutor="8-31,9-01,9-02,9-03,9-04,9-05";
            String  finish="1.0,0.9,2.0,1.5,1.5,1.7";

            map.put("日期",excutor);
            map.put("平均完成天数",finish);
            map.put("详情表", totalOverViewList);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }


    /**
     * 未完成数据
     */
    private Map selectUnfinishedTask(TaskCondition taskCondition) {
        map = new HashMap<>(16);
        try {
            //总任务数
            double count = 62D;
            //未完成数
            double unFinish = 13D;
            double percent = unFinish / count;
//        this.taskInfoMapper.selectUnfinishedTask(taskCondition);
            List<TotalOverView> totalOverViewList = new ArrayList<>();
            TotalOverView to1 = new TotalOverView();
            to1.setEndTime("2018-08-21");
            to1.setTaskName("施工计划");
            to1.setExecutor("张先生");
            to1.setTaskGroup("任务One");
            to1.setListView("项目评估");
            TotalOverView to2 = new TotalOverView();
            to2.setEndTime("2018-08-22");
            to2.setTaskName("深化设计");
            to2.setExecutor("王先生");
            to2.setTaskGroup("任务Two");
            to2.setListView("合同签订");
            totalOverViewList.add(to1);
            totalOverViewList.add(to2);

            map.put("未完成百分比", numChange(percent));
            map.put("未完成", unFinish);
            map.put("详情表", totalOverViewList);
        } catch (Exception e) {
            System.out.println("-------------未完成报错-----------");
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 已完成数据
     */
    private Map selectFinishedTask(TaskCondition taskCondition) {
        map = new HashMap<>(16);
        try {
            //总任务数
            double count = 62D;
            //未完成数
            double finish = 49D;
            double percent = finish / count;
//        this.taskInfoMapper.selectUnfinishedTask(taskCondition);
            List<TotalOverView> taskConditionsList = new ArrayList<>();
            TotalOverView to1 = new TotalOverView();
            to1.setSuccessTime("2018-08-23");
            to1.setTaskName("施工计划2");
            to1.setExecutor("李先生");
            to1.setTaskGroup("任务One");
            to1.setListView("项目评估");
            TotalOverView to2 = new TotalOverView();
            to2.setSuccessTime("2018-08-24");
            to2.setTaskName("深化设计2");
            to2.setExecutor("赵先生");
            to2.setTaskGroup("任务Two");
            to2.setListView("合同签订2");
            taskConditionsList.add(to1);
            taskConditionsList.add(to2);

            map.put("已完成百分比",numChange(percent));
            map.put("已完成", finish);
            map.put("详情表", taskConditionsList);
        } catch (Exception e) {
            System.out.println("已完成报错");
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 总数据
     */
    private Map selectTotalTask(TaskCondition taskCondition) {
        map = new HashMap<>(16);
        try {
            int count = 62;
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
    private Map selectExpireTask(TaskCondition taskCondition) {
        map = new HashMap<>(16);
        try {
            //总任务数
            double count = 62D;
            //今日到期
            double expire = 8D;
            double percent = expire / count;
//        this.taskInfoMapper.selectUnfinishedTask(taskCondition);
            List<TotalOverView> taskConditionsList = new ArrayList<>();
            TotalOverView to1 = new TotalOverView();
            to1.setCreateTime("2018-08-18");
            to1.setTaskName("施工计划3");
            to1.setExecutor("万先生");
            to1.setTaskGroup("任务One");
            to1.setListView("项目评估3");
            TotalOverView to2 = new TotalOverView();
            to2.setCreateTime("2018-08-19");
            to2.setTaskName("深化设计3");
            to2.setExecutor("亿先生");
            to2.setTaskGroup("任务Two");
            to2.setListView("合同签订3");
            taskConditionsList.add(to1);
            taskConditionsList.add(to2);

            map.put("已到期百分比",numChange(percent));
            map.put("已到期", expire);
            map.put("详情表", taskConditionsList);
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
    private Map selectOverdueTask(TaskCondition taskCondition) {
        map = new HashMap<>(16);
        try {
            //总任务数
            double count = 62D;
            //今日到期
            double overdue = 6D;
            double percent = overdue / count;
//        this.taskInfoMapper.selectUnfinishedTask(taskCondition);
            List<TotalOverView> taskConditionsList = new ArrayList<>();
            TotalOverView to1 = new TotalOverView();
            to1.setEndTime("2018-08-21");
            to1.setTaskName("施工计划4");
            to1.setExecutor("赵先生");
            to1.setTaskGroup("任务One");
            to1.setListView("项目评估4");
            TotalOverView to2 = new TotalOverView();
            to2.setEndTime("2018-08-22");
            to2.setTaskName("深化设计4");
            to2.setExecutor("黄先生");
            to2.setTaskGroup("任务Two");
            to2.setListView("合同签订4");
            taskConditionsList.add(to1);
            taskConditionsList.add(to2);

            map.put("已逾期百分比", numChange(percent));
            map.put("已逾期", overdue);
            map.put("详情表", taskConditionsList);
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
    private Map selectWaitClaimTask(TaskCondition taskCondition) {
        map = new HashMap<>(16);
        try {
            //总任务数
            double count = 62D;
            //今日到期
            double waitClaim = 10D;
            double percent = waitClaim / count;
//        this.taskInfoMapper.selectUnfinishedTask(taskCondition);
            List<TotalOverView> taskConditionsList = new ArrayList<>();
            TotalOverView to1 = new TotalOverView();
            to1.setEndTime("2018-08-25");
            to1.setTaskName("施工计划5");
            to1.setExecutor("孙先生");
            to1.setTaskGroup("任务One");
            to1.setListView("项目评估5");
            TotalOverView to2 = new TotalOverView();
            to2.setEndTime("2018-08-22");
            to2.setTaskName("深化设计5");
            to2.setExecutor("＃先生");
            to2.setTaskGroup("任务Two");
            to2.setListView("合同签订5");
            taskConditionsList.add(to1);
            taskConditionsList.add(to2);

            map.put("待认领百分比", numChange(percent));
            map.put("待认领", waitClaim);
            map.put("详情表", taskConditionsList);
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
    private Map selectPunctualityTask(TaskCondition taskCondition) {
        map = new HashMap<>(16);
        try {
            //总任务数
            double count = 62D;
            //未完成数
            double punctuality = 40D;
            double percent = punctuality / count;
//        this.taskInfoMapper.selectUnfinishedTask(taskCondition);
            List<TotalOverView> taskConditionsList = new ArrayList<>();
            TotalOverView to1 = new TotalOverView();
            to1.setEndTime("2018-08-21");
            to1.setTaskName("施工计划7");
            to1.setExecutor("冯先生");
            to1.setTaskGroup("任务One");
            to1.setListView("项目评估7");
            TotalOverView to2 = new TotalOverView();
            to2.setEndTime("2018-08-22");
            to2.setTaskName("深化设计7");
            to2.setExecutor("陈先生");
            to2.setTaskGroup("任务Two");
            to2.setListView("合同签订7");
            taskConditionsList.add(to1);
            taskConditionsList.add(to2);

            map.put("按时完成百分比",numChange(percent));
            map.put("按时完成", punctuality);
            map.put("详情表", taskConditionsList);
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
    private Map selectExpiredToCompleteTask(TaskCondition taskCondition) {
        map = new HashMap<>(16);
        try {
            //总任务数
            double count = 62D;
            //未完成数
            double expiredToComplete = 10D;
            double percent = expiredToComplete / count;
//        this.taskInfoMapper.selectUnfinishedTask(taskCondition);
            List<TotalOverView> taskConditionsList = new ArrayList<>();
            TotalOverView to1 = new TotalOverView();
            to1.setEndTime("2018-08-21");
            to1.setTaskName("施工计划8");
            to1.setExecutor("刘先生");
            to1.setTaskGroup("任务One");
            to1.setListView("项目评估8");
            to1.setOverdueNum(3);
            TotalOverView to2 = new TotalOverView();
            to2.setEndTime("2018-08-22");
            to2.setTaskName("深化设计8");
            to2.setExecutor("石先生");
            to2.setTaskGroup("任务Two");
            to2.setListView("合同签订8");
            to2.setOverdueNum(5);
            taskConditionsList.add(to1);
            taskConditionsList.add(to2);

            map.put("逾期完成百分比", numChange(percent));
            map.put("逾期完成", expiredToComplete);
            map.put("详情表", taskConditionsList);
        } catch (Exception e) {
            System.out.println("逾期完成报错");
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 任务按优先级分布
     */
    private Map selectPriorityTask(TaskCondition taskCondition) {
        map = new HashMap<>(16);
        try {
//        this.taskInfoMapper.selectUnfinishedTask(taskCondition);
            List<TaskDistribution> taskDistributionList = new ArrayList<>();
            TaskDistribution to1 = new TaskDistribution();
            to1.setTaskPrecedence("十万火急");
            to1.setTaskNum(30);
            TaskDistribution to2 = new TaskDistribution();
            to2.setTaskPrecedence("紧急");
            to2.setTaskNum(12);
            TaskDistribution to3 = new TaskDistribution();
            to3.setTaskPrecedence("普通");
            to3.setTaskNum(9);
            taskDistributionList.add(to1);
            taskDistributionList.add(to2);
            taskDistributionList.add(to3);
            List<TaskDistribution> taskDetaileList = new ArrayList<>();
            TaskDistribution to4 = new TaskDistribution();
            to4.setTaskName("零零壹");
            to4.setTaskPrecedence("十万火急");
            to4.setTaskCase("未完成");
            to4.setExecutor("韩梅");
            TaskDistribution to5 = new TaskDistribution();
            to5.setTaskName("零零贰");
            to5.setTaskPrecedence("普通");
            to5.setTaskCase("已完成");
            to5.setExecutor("李雷");
            TaskDistribution to6 = new TaskDistribution();
            to6.setTaskName("零零叁");
            to6.setTaskPrecedence("十万火急");
            to6.setTaskCase("未完成");
            to6.setExecutor("韩梅");
            taskDetaileList.add(to4);
            taskDetaileList.add(to5);
            taskDetaileList.add(to6);
            double count=0 ;
            for (TaskDistribution t:taskDistributionList) {
                count+=t.getTaskNum();
            }
            //获取格式化对象
            NumberFormat nt = NumberFormat.getPercentInstance();
            //设置百分数精确度2即保留两位小数
            nt.setMinimumFractionDigits(2);
            for (TaskDistribution t:taskDistributionList) {
                map.put(t.getTaskPrecedence(),nt.format(t.getTaskNum()/count));
            }
            map.put("详情表", taskDistributionList);
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
    private Map selectExcutorTask(TaskCondition taskCondition) {
        map = new HashMap<>(16);
        try {
//        this.taskInfoMapper.selectUnfinishedTask(taskCondition);
            List<TaskDistribution> taskDistributionList = new ArrayList<>();
            TaskDistribution to1 = new TaskDistribution();
            to1.setExecutor("李雷");
            to1.setTaskNum(10);
            TaskDistribution to2 = new TaskDistribution();
            to2.setExecutor("韩梅");
            to2.setTaskNum(5);
            taskDistributionList.add(to1);
            taskDistributionList.add(to2);
            double count=0 ;
            for (TaskDistribution t:taskDistributionList) {
                count+=t.getTaskNum();
            }
            for (TaskDistribution t:taskDistributionList) {
                 map.put(t.getExecutor(),numChange(t.getTaskNum()/count));
            }
            map.put("详情表", taskDistributionList);
        } catch (Exception e) {
            System.out.println("按完成情况分布报错");
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 任务按任务分组分布
     */
    private Map selectGroupByTask(TaskCondition taskCondition) {
        map = new HashMap<>(16);
        try {
//        this.taskInfoMapper.selectUnfinishedTask(taskCondition);
            List<TaskDistribution> taskDistributionList = new ArrayList<>();
            TaskDistribution to1 = new TaskDistribution();
            to1.setListView("任务1");
            to1.setTaskBad(12);
            to1.setTaskSuccess(4);
            TaskDistribution to2 = new TaskDistribution();
            to2.setListView("任务2");
            to2.setTaskBad(22);
            to2.setTaskSuccess(8);
            taskDistributionList.add(to1);
            taskDistributionList.add(to2);
            //创建String对象 拼接前端需要的分组和任务数量数据
            StringBuilder groupName = new StringBuilder();
            StringBuilder unFinishNum = new StringBuilder();
            StringBuilder finishNum = new StringBuilder();
            //循环获取已完成和未完成 组名和任务数
            for (TaskDistribution t : taskDistributionList) {
                groupName.append(t.getListView()).append(",");
                unFinishNum.append(t.getTaskBad()).append(",");
                finishNum.append(t.getTaskSuccess()).append(",");
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
     * 任务按完成情况分布
     */
    private Map selectCompletionTask(TaskCondition taskCondition) {
        map = new HashMap<>(16);
        try {
            //总任务数
            double count = 62D;
            //未完成数
            double unFinishd = 10D;
            //已完成数
            double finishd = 52D;
            //计算小数
            double percent = unFinishd / count;
            double percentT = finishd / count;
//        this.taskInfoMapper.selectUnfinishedTask(taskCondition);
            List<TaskDistribution> taskDistributionList = new ArrayList<>();
            TaskDistribution to1 = new TaskDistribution();
            to1.setTaskCase("未完成");
            to1.setTaskNum(10);
            TaskDistribution to2 = new TaskDistribution();
            to2.setTaskCase("已完成");
            to2.setTaskNum(52);
            taskDistributionList.add(to1);
            taskDistributionList.add(to2);

            map.put("未完成百分比",numChange(percent));
            map.put("已完成百分比", numChange(percentT));
            map.put("详情表", taskDistributionList);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    private Map selectTimeOverdue(TaskCondition taskCondition) {
        map = new HashMap<>(16);
        try {
//        this.taskInfoMapper.selectUnfinishedTask(taskCondition);
            List<TotalOverView> totalOverViewList = new ArrayList<>();
            TotalOverView to1 = new TotalOverView();
            to1.setExecutor("吴XX");
            to1.setTaskNum("10");
            TotalOverView to2 = new TotalOverView();
            to2.setExecutor("郑VV");
            to2.setTaskNum("22");
            TotalOverView to10 = new TotalOverView();
            to10.setExecutor(null);
            to10.setTaskNum("15");
            totalOverViewList.add(to1);
            totalOverViewList.add(to2);
            totalOverViewList.add(to10);
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            for (TotalOverView t : totalOverViewList) {
                sb1.append(t.getExecutor()== null?"待认领":t.getExecutor()).append(",");
                sb2.append(t.getTaskNum()).append(",");
            }
            if (sb1.length() > 0 && sb2.length() > 0){
                map.put("执行者", percentChange(sb1.toString()));
                map.put("任务数", percentChange(sb2.toString()));
            }
            //详情表信息
            List<TotalOverView> totalOverViewList1 = new ArrayList<>();
            TotalOverView to3 = new TotalOverView();
            to3.setTaskName("零零1");
            to3.setEndTime("2018-08-21");
            to3.setExecutor("赑屃先生");
            to3.setTaskGroup("任务19");
            to3.setListView("分组8");
            TotalOverView to4 = new TotalOverView();
            to4.setTaskName("零零2");
            to4.setEndTime("2018-09-11");
            to4.setExecutor("饕餮先生");
            to4.setTaskGroup("任务15");
            to4.setListView("分组5");
            totalOverViewList1.add(to3);
            totalOverViewList1.add(to4);

            map.put("详情表", totalOverViewList1);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    private Map selectTimeUnfinish(TaskCondition taskCondition) {
        map = new HashMap<>(16);
        try {
//        this.taskInfoMapper.selectUnfinishedTask(taskCondition);
            List<TotalOverView> totalOverViewList = new ArrayList<>();
            TotalOverView to1 = new TotalOverView();
            to1.setExecutor("赵XX");
            to1.setTaskNum("20");
            TotalOverView to2 = new TotalOverView();
            to2.setExecutor("李VV");
            to2.setTaskNum("30");
            TotalOverView to10 = new TotalOverView();
            to10.setExecutor(null);
            to10.setTaskNum("5");
            totalOverViewList.add(to1);
            totalOverViewList.add(to2);
            totalOverViewList.add(to10);
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            for (TotalOverView t : totalOverViewList) {
                sb1.append(t.getExecutor()== null?"待认领":t.getExecutor()).append(",");
                sb2.append(t.getTaskNum()).append(",");
            }
            if (sb1.length() > 0 && sb2.length() > 0){
                map.put("执行者", percentChange(sb1.toString()));
                map.put("任务数", percentChange(sb2.toString()));
            }
            //详情表信息
            List<TotalOverView> totalOverViewList1 = new ArrayList<>();
            TotalOverView to3 = new TotalOverView();
            to3.setTaskName("零零妖");
            to3.setEndTime("2018-08-21");
            to3.setExecutor("楚先生");
            to3.setTaskGroup("任务19");
            to3.setListView("分组8");
            TotalOverView to4 = new TotalOverView();
            to4.setTaskName("零零红中");
            to4.setEndTime("2018-09-11");
            to4.setExecutor("魏先生");
            to4.setTaskGroup("任务15");
            to4.setListView("分组5");
            totalOverViewList1.add(to3);
            totalOverViewList1.add(to4);

            map.put("详情表", totalOverViewList1);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    private Map selectTimeFinish(TaskCondition taskCondition) {
        map = new HashMap<>(16);
        try {
//        this.taskInfoMapper.selectUnfinishedTask(taskCondition);
            List<TotalOverView> totalOverViewList = new ArrayList<>();
            TotalOverView to1 = new TotalOverView();
            to1.setExecutor("张XX");
            to1.setTaskNum("10");
            TotalOverView to2 = new TotalOverView();
            to2.setExecutor("李XX");
            to2.setTaskNum("22");
            totalOverViewList.add(to1);
            totalOverViewList.add(to2);
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            for (TotalOverView t : totalOverViewList) {
                sb1.append(t.getExecutor()).append(",");
                sb2.append(t.getTaskNum()).append(",");
            }
            if (sb1.length() > 0 && sb2.length() > 0){
                map.put("执行者", percentChange(sb1.toString()));
                map.put("任务数", percentChange(sb2.toString()));
            }
            //详情表信息
            List<TotalOverView> totalOverViewList1 = new ArrayList<>();
            TotalOverView to3 = new TotalOverView();
            to3.setTaskName("零零发");
            to3.setSuccessTime("2018-08-31");
            to3.setExecutor("冯先生");
            to3.setTaskGroup("任务10");
            to3.setListView("分组1");
            TotalOverView to4 = new TotalOverView();
            to4.setTaskName("零零白板");
            to4.setSuccessTime("2018-09-01");
            to4.setExecutor("陈先生");
            to4.setTaskGroup("任务11");
            to4.setListView("分组2");
            totalOverViewList1.add(to3);
            totalOverViewList1.add(to4);

            map.put("详情表", totalOverViewList1);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }
    /**
     *高频参与任务
     */
    private Map selectMoreParticipation(TaskCondition taskCondition) {
        map = new HashMap<>(16);
        try {
//        this.taskInfoMapper.selectUnfinishedTask(taskCondition);
            List<TotalOverView> totalOverViewList = new ArrayList<>();
            TotalOverView to1 = new TotalOverView();
            to1.setTaskName("78910J");
            to1.setDymamicNum(11);
            to1.setEndTime("2018-08-21 18:40");
            to1.setExecutor("Z先生");
            to1.setTaskGroup("分组1");
            to1.setListView("任务8");
            TotalOverView to2 = new TotalOverView();
            to2.setTaskName("12345");
            to2.setDymamicNum(15);
            to2.setEndTime("2018-08-22 18:40");
            to2.setExecutor("F先生");
            to2.setTaskGroup("分组2");
            to2.setListView("任务5");
            totalOverViewList.add(to1);
            totalOverViewList.add(to2);

          String[] excutor={"X先生","Y先生","Z先生"};
          int[] excutorNum={12,10,7};

            map.put("执行者",excutor);
            map.put("任务数",excutorNum);
            map.put("详情表", totalOverViewList);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }
    /**
     * 截至时间完成
     */
    private Map selectEndTimeTaskFinish(TaskCondition taskCondition) {
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
//        this.taskInfoMapper.selectUnfinishedTask(taskCondition);
            List<TaskDistribution> taskDistributionList = new ArrayList<>();
            TaskDistribution to1 = new TaskDistribution();

            to1.setTaskBad(12);
            to1.setTaskSuccess(4);
            TaskDistribution to2 = new TaskDistribution();
            to2.setListView("任务2");
            to2.setTaskBad(22);
            to2.setTaskSuccess(8);
            taskDistributionList.add(to1);
            taskDistributionList.add(to2);
            //创建String对象 拼接前端需要的分组和任务数量数据
            StringBuilder groupName = new StringBuilder();
            StringBuilder unFinishNum = new StringBuilder();
            StringBuilder finishNum = new StringBuilder();
            //循环获取已完成和未完成 组名和任务数
            for (TaskDistribution t : taskDistributionList) {
                groupName.append(t.getListView()).append(",");
                unFinishNum.append(t.getTaskBad()).append(",");
                finishNum.append(t.getTaskSuccess()).append(",");
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
    private Map selectUpdateTaskTime(TaskCondition taskCondition) {
        map = new HashMap<>(16);
        try {
//        this.taskInfoMapper.selectUnfinishedTask(taskCondition);


            //详情表信息
            List<TotalOverView> totalOverViewList1 = new ArrayList<>();
            TotalOverView to3 = new TotalOverView();
            to3.setTaskName("003");
            to3.setCreateTime("2018-08-20 15:30");
            to3.setEndTime("2018-08-21  18:40");
            to3.setExecutor("X先生");
            to3.setTaskGroup("任务8");
            to3.setListView("分组1");
            TotalOverView to4 = new TotalOverView();
            to4.setTaskName("002");
            to4.setCreateTime("2018-09-01 15:30");
            to4.setEndTime("2018-09-10 17:40");
            to4.setExecutor("Y先生");
            to4.setTaskGroup("任务5");
            to4.setListView("分组6");
            totalOverViewList1.add(to3);
            totalOverViewList1.add(to4);

            //chart数据
            String[] excutor={"W先生","G先生","L先生"};
            int[] excutorNum={11,10,7};

            map.put("执行者",excutor);
            map.put("任务数",excutorNum);
            map.put("详情表", totalOverViewList1);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 期间截止任务分成员完成情况
     */
    private Map selectTaskByExcutor(TaskCondition taskCondition) {
        map =new HashMap<>();
        try {
            List<TotalOverView> totalOverViewList = new ArrayList<>();
            TotalOverView to1 = new TotalOverView();
            to1.setEndTime("2018-08-21");
            to1.setTaskName("施工计划");
            to1.setTaskCase("未完成");
            to1.setSuccessTime("2018-08-21 18:00");
            to1.setExecutor("王先生");
            TotalOverView to2 = new TotalOverView();
            to2.setEndTime("2018-08-22");
            to2.setTaskName("深化设计");
            to2.setSuccessTime("2018-08-22 18:00");
            to2.setExecutor("玉先生");
            totalOverViewList.add(to1);
            totalOverViewList.add(to2);

            String  excutor="X,Y,Z,W";
            String  unFinish="1,52,12,4";
            String  finish="10,5,2,44";

            map.put("执行者",excutor);
            map.put("未完成任务",unFinish);
            map.put("已完成任务",finish);
            map.put("详情表",totalOverViewList);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    private Map selectTaskByEndTime(TaskCondition taskCondition) {
        map =new HashMap<>();
        try {
            List<TotalOverView> totalOverViewList = new ArrayList<>();
            TotalOverView to1 = new TotalOverView();
            to1.setEndTime("2018-08-21  10:00");
            to1.setTaskName("V计划");
            to1.setTaskCase("未完成");
            to1.setSuccessTime("2018-08-21 18:00");
            to1.setExecutor("王先生");
            TotalOverView to2 = new TotalOverView();
            to2.setEndTime("2018-08-22 10:00");
            to2.setTaskName("Z设计");
            to2.setSuccessTime("2018-08-22 18:00");
            to2.setExecutor("玉先生");
            totalOverViewList.add(to1);
            totalOverViewList.add(to2);

            String  excutor="8-31,9-01,9-02,9-03,9-04,9-05";
            String  unFinish="1,2,3,4,5,6,7";
            String  finish="8,9,10,15,15";

            map.put("日期",excutor);
            map.put("未完成任务数",unFinish);
            map.put("已完成任务数",finish);
            map.put("详情表",totalOverViewList);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    /**
     *项目进展走势
     */
    private Map selectProjectProgress(TaskCondition taskCondition) {
        map=new HashMap<>(16);
        try {
            List<TotalOverView> totalOverViewList = new ArrayList<>();
            TotalOverView to1 = new TotalOverView();
            to1.setCreateTime("2018-08-21");
            to1.setTaskName("RZ计划");
            to1.setExecutor("H先生");
            TotalOverView to2 = new TotalOverView();
            to2.setCreateTime("2018-08-22");
            to2.setTaskName("XC设计");
            to2.setExecutor("A先生");
            totalOverViewList.add(to1);
            totalOverViewList.add(to2);

            String  excutor="8-31,9-01,9-02,9-03,9-04,9-05";
            String  finish="1,2,3,4,5,6,7";
            String  unFinish="8,9,10,15,15";

            map.put("日期",excutor);
            map.put("累计总任务数",unFinish);
            map.put("累计完成任务数",finish);
            map.put("详情表",totalOverViewList);
        } catch (Exception e) {
            map=null;
            e.printStackTrace();
        }
        return  map;
    }

    /**
     * 每日完成任务量
     */
    private Map selectDayFinish(TaskCondition taskCondition) {
        map = new HashMap<>(16);
        try {
//        this.taskInfoMapper.selectUnfinishedTask(taskCondition);
            List<TotalOverView> totalOverViewList = new ArrayList<>();
            TotalOverView to1 = new TotalOverView();
            to1.setSuccessTime("2018-08-21");
            to1.setTaskName("施工计划");
            to1.setExecutor("张先生");
            to1.setTaskDayNum("1");
            TotalOverView to2 = new TotalOverView();
            to2.setEndTime("2018-08-22");
            to2.setTaskName("深化设计");
            to2.setExecutor("王先生");
            to2.setTaskGroup("任务Two");
            to2.setListView("合同签订");
            to2.setTaskDayNum("2");
            totalOverViewList.add(to1);
            totalOverViewList.add(to2);

            String  excutor="8-31,9-01,9-02,9-03,9-04,9-05";
            String  finish="8,9,10,15,15";

            map.put("日期",excutor);
            map.put("每日完成任务数",finish);
            map.put("详情表", totalOverViewList);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    /**
     *任务燃尽图
     */
    private Map selectTaskBurnOut(TaskCondition taskCondition) {
        map = new HashMap<>(16);
        try {
//        this.taskInfoMapper.selectUnfinishedTask(taskCondition);
            List<TotalOverView> totalOverViewList = new ArrayList<>();
            TotalOverView to1 = new TotalOverView();
            to1.setCreateTime("2018-08-21 18:00");
            to1.setTaskName("施工计划");
            to1.setChange("新增");
            to1.setTaskNum("+1");
            TotalOverView to2 = new TotalOverView();
            to2.setCreateTime("2018-08-22  17:00");
            to2.setTaskName("深化设计");
            to2.setChange("完成");
            to2.setTaskNum("-1");
            totalOverViewList.add(to1);
            totalOverViewList.add(to2);

            String  dayNum="8-31,9-01,9-02,9-03,9-04,9-05";
            String  reality="8.0,2.9,4.0,5.5,7.5,8.7";
            String  ideality="1.0,0.9,2.0,1.5,1.5,1.7";

            map.put("日期",dayNum);
            map.put("实际剩余任务数",ideality);
            map.put("理想剩余任务数",reality);
            map.put("详情表", totalOverViewList);
        } catch (Exception e) {
            map = null;
            e.printStackTrace();
        }
        return map;
    }

    /**
     *不同任务分组已完成任务量
     */
    private Map selectTaskDifSuccess(TaskCondition taskCondition) {
        map = new HashMap<>(16);
        try {
//        this.taskInfoMapper.selectUnfinishedTask(taskCondition);
            List<TotalOverView> totalOverViewList = new ArrayList<>();
            TotalOverView to1 = new TotalOverView();
            to1.setTaskGroup("任务1");
            to1.setTaskNum("15");
            TotalOverView to2 = new TotalOverView();
            to2.setTaskGroup("任务2");
            to2.setTaskNum("18");
            totalOverViewList.add(to1);
            totalOverViewList.add(to2);

            String taskName="任务1,任务2";
            String  TaskFinishNum="15,18";

            map.put("任务分组",taskName);
            map.put("已完成任务数",TaskFinishNum);
            map.put("详情表", totalOverViewList);
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

}
