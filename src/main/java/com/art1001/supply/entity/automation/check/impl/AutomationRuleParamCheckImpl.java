package com.art1001.supply.entity.automation.check.impl;

import com.art1001.supply.entity.automation.check.AutomationRuleParamCheck;
import com.art1001.supply.entity.automation.constans.AutomationRuleConstans;
import com.art1001.supply.exception.AutomationRuleParamException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * @Description 校验
 * @Date:2019/8/7 11:41
 * @Author shaohua
 **/
@Configuration
public class AutomationRuleParamCheckImpl {


    /**
     * 自动化规则的参数校验 ---- 快速失败模式
     * 如果需要其他校验模式，请在此类下面添加方法，并且返回AutomationRuleParamCheck 实例即可
     * @return AutomationRuleParamCheck 接口实现实例
     */
    @Bean
    public AutomationRuleParamCheck fastFail(){
        return check -> {
            boolean incorrect;
            Object newValue;

            //验证当前添加的自动化规则参数中 conditionValues是否可以为空
            boolean valueNotNull = AutomationRuleConstans.END_TIME.equals(check.getConditionName())
                        || AutomationRuleConstans.REPEAT.equals(check.getConditionName())
                        || AutomationRuleConstans.PRIORITY.equals(check.getConditionName())
                        || AutomationRuleConstans.SETTING_EXECUTORS.equals(check.getConditionName());
            if(valueNotNull){
                if(StringUtils.isNotEmpty(check.getConditionValue())){
                    throw new AutomationRuleParamException("值不能为空！");
                }
            }

            //验证如果条件名称是重复性的时候的参数是否合法
            if(check.getConditionName().equals(AutomationRuleConstans.REPEAT)){
                newValue = check.getConditionValue();
                incorrect = !AutomationRuleConstans.TRUE.equals(newValue) && !AutomationRuleConstans.FALSE.equals(newValue);
                if(incorrect){
                    throw new AutomationRuleParamException("重复性只能传 ‘true’ 和 ‘false’！");
                }
            }

            //验证如果条件名称是优先级的时候的参数是否合法
            if(check.getConditionName().equals(AutomationRuleConstans.PRIORITY)){
                newValue = String.valueOf(check.getConditionValue());
                incorrect = (!newValue.equals(AutomationRuleConstans.URGENT)
                        && !newValue.equals(AutomationRuleConstans.VERY_URGENT)
                        && !newValue.equals(AutomationRuleConstans.ORDINARY));
                if(incorrect){
                    throw new AutomationRuleParamException("优先级只能传 ‘ordinary’、‘urgent’、‘veryUrgent’！");
                }
            }

            //验证如果条件名称是设置执行人的时候的参数是否合法
            if(check.getConditionName().equals(AutomationRuleConstans.SETTING_EXECUTORS)){
                String executorId = String.valueOf(check.getConditionValue());
                int executorIdLength = executorId.length();
                incorrect = (executorIdLength > 32 || executorIdLength < 32);
                if(incorrect){
                    throw new AutomationRuleParamException("用户id不正确！");
                }
            }

            //验证如果条件名称是截止时间的时候的参数是否合法
            if(check.getConditionName().equals(AutomationRuleConstans.END_TIME)){
                try {
                    new BigDecimal(check.getConditionValue());
                } catch (NumberFormatException e){
                    throw new AutomationRuleParamException("截止时间的值不正确！");
                }
            }

            //验证结果是否合法
            if(StringUtils.isNotEmpty(check.getDefaultAssign()) || StringUtils.isNotEmpty(check.getAutomaticJump())){
                int assignLength;
                int jumpLength;
                boolean assignIsTrue = false;
                boolean jumpIsTrue = false;
                if(StringUtils.isNotEmpty(check.getDefaultAssign())){
                    assignLength = check.getDefaultAssign().length();
                    assignIsTrue = assignLength > 32 || assignLength < 32;
                }
                if(StringUtils.isNotEmpty(check.getAutomaticJump())){
                    jumpLength = check.getAutomaticJump().length();
                    jumpIsTrue = jumpLength > 32 || jumpLength < 32;
                }
                if(assignIsTrue || jumpIsTrue){
                    throw new AutomationRuleParamException("结果值不正确！");
                }
            } else {
                throw new AutomationRuleParamException("结果值不能为空！");
            }
            return 1;
        };
    }
}
