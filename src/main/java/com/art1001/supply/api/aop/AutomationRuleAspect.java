package com.art1001.supply.api.aop;

import com.art1001.supply.entity.automation.AutomationAnnotationValue;
import com.art1001.supply.entity.automation.AutomationRule;
import com.art1001.supply.entity.automation.check.AutomationRuleTriggerCheck;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.service.automation.AutomationRuleStartup;
import com.art1001.supply.service.automation.AutomationRulesService;
import com.art1001.supply.service.task.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
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
        //获取切点的方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //方法注解
        com.art1001.supply.annotation.AutomationRule annotation = methodSignature.getMethod().getAnnotation(com.art1001.supply.annotation.AutomationRule.class);
        AutomationAnnotationValue automationAnnotationValue = genValue(methodSignature, joinPoint);
        //检测当前接口是否是该条规则的启动条件 如果是返回 1 否则返回0
        Integer result = automationRuleTriggerCheck.checkAutomationCanStartup(automationAnnotationValue, annotation.trigger());
        if(result >= 1){
            Task task = taskService.getById(automationAnnotationValue.getTaskId());
            AutomationRule newestRuleByCreateTime = automationRulesService.getNewestRuleByCreateTime(task.getTaskMenuId(), task.getProjectId());
            startup.startupAutomation(newestRuleByCreateTime,task.getTaskId());
        }
    }

    private AutomationAnnotationValue genValue(MethodSignature methodSignature, JoinPoint joinPoint){
        //获取方法上注解的值
        com.art1001.supply.annotation.AutomationRule annotation = methodSignature.getMethod().getAnnotation(com.art1001.supply.annotation.AutomationRule.class);
        //获取方法参数定义名字
        String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
        Expression expression = parser.parseExpression(annotation.value());
        //此上下文基于反射来解析属性，方法和字段，使用所有适用策略的标准实现。
        EvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        if(paramNames != null && paramNames.length > 0){
            for(int i = 0 ; i < args.length ; i++) {
                if(StringUtils.isNotEmpty(paramNames[i])){
                    //在此评估上下文中将命名变量设置为指定值
                    context.setVariable(paramNames[i], args[i]);
                } else {
                    context.setVariable(null, args[i]);
                }
            }
        }
        AutomationAnnotationValue automationAnnotationValue = new AutomationAnnotationValue();
        Object value = expression.getValue(context);
        if(StringUtils.isNotEmpty(annotation.objectValue())){
            Expression expressionObjVal = parser.parseExpression(annotation.objectValue());
            Object objValue = expressionObjVal.getValue(context);
            automationAnnotationValue.setObjectValue(String.valueOf(objValue));
        }
        automationAnnotationValue.setTaskId(String.valueOf(value));
        return automationAnnotationValue;
    }
}
