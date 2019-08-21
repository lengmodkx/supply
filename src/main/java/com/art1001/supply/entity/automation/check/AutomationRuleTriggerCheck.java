package com.art1001.supply.entity.automation.check;

import com.art1001.supply.entity.automation.AutomationAnnotationValue;
import com.art1001.supply.entity.automation.AutomationRule;

/**
 * @Description
 * @Date:2019/8/19 17:53
 * @Author ddm
 **/
@FunctionalInterface
public interface AutomationRuleTriggerCheck {
    
    /**
     * 检测当前接口是否是该条规则的启动条件 如果是返回 1 否则返回0
     * @author heShaoHua
     * @describe 如果任一参数为空则返回-1
     * @param value 任务id和触发事件的值
     * @param triggerCondition 自动化规则的触发条件
     * @updateInfo 暂无
     * @date 2019/8/19 17:43
     * @return 是否可以启动
     */
    Integer checkAutomationCanStartup(AutomationAnnotationValue value, String triggerCondition);
}