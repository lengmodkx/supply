package com.art1001.supply.service.automation;

import com.art1001.supply.entity.automation.AutomationRule;

/**
 * @Description
 * @Date:2019/8/20 14:28
 * @Author ddm
 **/
@FunctionalInterface
public interface AutomationRuleStartup {


    /**
     * 执行自动化规则流程
     * @author heShaoHua
     * @describe 如果automationRule为空返回-1 ，执行成功返回 1 否则返回0
     * @param automationRule 自动化规则流程信息
     * @param taskId 任务id
     * @updateInfo 暂无
     * @date 2019/8/20 14:29
     * @return 是否执行成功
     */
    Integer startupAutomation(AutomationRule automationRule, String taskId);
}