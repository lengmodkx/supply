package com.art1001.supply.entity.automation.check;

import com.art1001.supply.entity.automation.AutomationRuleCheck;
import com.art1001.supply.entity.automation.constans.AutomationRuleConstans;
import com.art1001.supply.exception.AutomationRuleParamException;
import com.art1001.supply.util.Stringer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @Description 校验
 * @Date:2019/8/7 11:41
 * @Author shaohua
 **/
@Component
public class AutomationRuleParamCheckImpl {


    @Bean
    public AutomationRuleCheck fastFail(){
        return check -> {
            boolean incorrect;
            Object newValue;

            //验证当前添加的自动化规则参数中 conditionValues是否可以为空
            boolean valueNotNull = AutomationRuleConstans.END_TIME.equals(check.getConditionName())
                        || AutomationRuleConstans.REPEAT.equals(check.getConditionName())
                        || AutomationRuleConstans.PRIORITY.equals(check.getConditionName())
                        || AutomationRuleConstans.SETTING_EXECUTORS.equals(check.getConditionName());
            if(valueNotNull){
                if(Stringer.isNullOrEmpty(check.getConditionValue())){
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
            if(Stringer.isNotNullOrEmpty(check.getDefaultAssign()) || Stringer.isNotNullOrEmpty(check.getAutomaticJump())){
                int assignLength;
                int jumpLength;
                boolean assignIsTrue = false;
                boolean jumpIsTrue = false;
                if(Stringer.isNotNullOrEmpty(check.getDefaultAssign())){
                    assignLength = check.getDefaultAssign().length();
                    assignIsTrue = assignLength > 32 || assignLength < 32;
                }
                if(Stringer.isNotNullOrEmpty(check.getAutomaticJump())){
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
