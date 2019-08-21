package com.art1001.supply.api.aop;

import com.art1001.supply.entity.automation.AutomationAnnotationValue;
import com.art1001.supply.entity.automation.AutomationRule;
import com.art1001.supply.entity.automation.check.AutomationRuleTriggerCheck;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.service.automation.AutomationRuleStartup;
import com.art1001.supply.service.automation.AutomationRulesService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.util.Stringer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @Description
 * @Date:2019/8/19 16:05
 * @Author heshaohua
 **/
@Aspect
@Component
@Slf4j
public class AutomationRuleAspect {

    /**
     * 用于SpEL表达式解析.
     */
    private SpelExpressionParser parser = new SpelExpressionParser();

    /**
     * 用于获取方法参数定义名字.
     */
    private DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Resource
    private AutomationRuleTriggerCheck automationRuleTriggerCheck;

    @Resource
    private AutomationRulesService automationRulesService;

    @Resource
    private TaskService taskService;

    @Resource
    private AutomationRuleStartup startup;

    @Pointcut("@annotation(com.art1001.supply.annotation.AutomationRule)")
    public void test(){}

    @Transactional(rollbackFor = Exception.class)
    @AfterReturning(value = "test()")
    public void before(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        com.art1001.supply.annotation.AutomationRule annotation = methodSignature.getMethod().getAnnotation(com.art1001.supply.annotation.AutomationRule.class);
        AutomationAnnotationValue automationAnnotationValue = genValue(methodSignature, joinPoint);
        Integer result = automationRuleTriggerCheck.checkAutomationCanStartup(automationAnnotationValue, annotation.trigger());
        if(result >= 1){
            Task task = taskService.getById(automationAnnotationValue.getTaskId());
            AutomationRule newestRuleByCreateTime = automationRulesService.getNewestRuleByCreateTime(task.getTaskMenuId(), task.getProjectId());
            startup.startupAutomation(newestRuleByCreateTime,task.getTaskId());
        }
    }

    private AutomationAnnotationValue genValue(MethodSignature methodSignature, JoinPoint joinPoint){
        com.art1001.supply.annotation.AutomationRule annotation = methodSignature.getMethod().getAnnotation(com.art1001.supply.annotation.AutomationRule.class);
        String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
        Expression expression = parser.parseExpression(annotation.value());
        EvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        if(paramNames != null && paramNames.length > 0){
            for(int i = 0 ; i < args.length ; i++) {
                if(Stringer.isNotNullOrEmpty(paramNames[i])){
                    context.setVariable(paramNames[i], args[i]);
                } else {
                    context.setVariable(null, args[i]);
                }
            }
        }
        Object value = expression.getValue(context);
        Expression expressionObjVal = parser.parseExpression(annotation.objectValue());
        Object objValue = expressionObjVal.getValue(context);
        AutomationAnnotationValue automationAnnotationValue = new AutomationAnnotationValue();
        automationAnnotationValue.setTaskId(String.valueOf(value));
        automationAnnotationValue.setObjectValue(String.valueOf(objValue));
        return automationAnnotationValue;
    }
}
