package com.art1001.supply.entity.automation.check;

import com.art1001.supply.entity.automation.AutomationRule;
import com.art1001.supply.exception.AutomationRuleParamException;

/**
 * @Description 校验
 * @Date:2019/8/7 11:32
 * @Author ddm
 **/
public interface AutomationRuleParamCheck {

    /**
     * 校验自动化规则参数的正确性
     * @author heShaoHua
     * @describe 用于创建AutomationRule时API接口参数的校验，如果校验通过返回1，否则抛出异常，并给出异常信息。
     * @param automationRule 自动化规则对象参数
     * @throws AutomationRuleParamException 参数错误异常
     * @updateInfo 暂无
     * @date 2019/8/7 11:36
     * @return 如果校验通过返回1
     */
    Integer checkAutomationRule(AutomationRule automationRule) throws AutomationRuleParamException;
}