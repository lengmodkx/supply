package com.art1001.supply.entity.automation.check.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.automation.AutomationRule;
import com.art1001.supply.entity.automation.check.AutomationRuleTriggerCheck;
import com.art1001.supply.entity.automation.constans.AutomationRuleConstans;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.service.automation.AutomationRulesService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.util.Stringer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @Description
 * @Date:2019/8/20 09:54
 * @Author heshaohua
 **/
@Configuration
public class AutomationRuleTriggerCheckImpl {

    /**
     * 注入任务逻辑层Bean
     */
    @Resource
    private TaskService taskService;

    /**
     * 注入自动化规则逻辑层Bean
     */
    @Resource
    private AutomationRulesService automationRulesService;

    /**
     * 校验当前请求是否可以启动自动化流程
     * @return AutomationRuleTriggerCheck 接口实例
     */
    @Bean
    public AutomationRuleTriggerCheck checkAutomationRuleTrigger(){
        return (annotationValue,tr) -> {
            if(Stringer.isNullOrEmpty(annotationValue.getTaskId()) || Stringer.isNullOrEmpty(tr)){
                return -1;
            }

            Task task = taskService.getById(annotationValue.getTaskId());
            if(task == null){
                return -1;
            }

            //获取到该列表的自动化规则条数
            Integer autoCount = automationRulesService.checkTaskMenuObjectCount(task.getTaskMenuId(), task.getProjectId());
            if(autoCount <= 0){
                return 0;
            }
            //获取到menuId列表的自动化规则中最新的一条
            AutomationRule newestRuleByCreateTime = automationRulesService.getNewestRuleByCreateTime(task.getTaskMenuId(), task.getProjectId());
            if(newestRuleByCreateTime == null){
                return -1;
            }

            //判断当前触发条件是否可以出发工作流
            String conditionName = newestRuleByCreateTime.getConditionName();
            if(AutomationRuleConstans.ALL.equals(newestRuleByCreateTime.getConditionName())){
                return 1;
            }
            if(conditionName.equals(tr)){
                boolean valueEqual = true;
                //库中保存的值
                String conditionValue = newestRuleByCreateTime.getConditionValue();
                //接口传进的值
                String objValue = annotationValue.getObjectValue();

                //如果conditionName 为优先级 校验该接口操作是否可以启动流程
                if(AutomationRuleConstans.PRIORITY.equals(conditionName)){
                    if(AutomationRuleConstans.ORDINARY.equals(conditionValue)){
                        valueEqual = AutomationRuleConstans.ORDINARY_CN.equals(objValue);
                    }
                    if(AutomationRuleConstans.VERY_URGENT.equals(conditionValue)){
                        valueEqual = AutomationRuleConstans.VERY_URGENT_CN.equals(objValue);
                    }
                    if(AutomationRuleConstans.URGENT.equals(conditionValue)){
                        valueEqual = AutomationRuleConstans.URGENT_CN.equals(objValue);
                    }
                }

                //如果conditionName 为 重复性 校验该接口操作是否可以启动流程
                if(AutomationRuleConstans.REPEAT.equals(conditionName)){
                    if(AutomationRuleConstans.FALSE.equals(conditionValue)){
                        valueEqual = AutomationRuleConstans.NO_REPEAT.equals(objValue);
                    }

                    if(AutomationRuleConstans.TRUE.equals(conditionValue)){
                        valueEqual = !AutomationRuleConstans.NO_REPEAT.equals(objValue);
                    }
                }

                //如果conditionName 为 设置执行人或者设置截止时间 校验该接口操作是否可以启动流程
                if(AutomationRuleConstans.SETTING_EXECUTORS.equals(conditionName) || AutomationRuleConstans.END_TIME.equals(conditionName)){
                    valueEqual = conditionValue.equals(objValue);
                }
                return valueEqual ? 1 : 0;
            }
            return 0;
        };
    }

}
