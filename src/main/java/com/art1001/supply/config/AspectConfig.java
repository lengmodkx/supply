package com.art1001.supply.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AspectConfig {

    @Pointcut("execution(public * com.art1001.supply.controller.*.*(..)) && @annotation(com.art1001.supply.annotation.Todo)")
    public void todo(){}


    @After("todo()")
    public void doAfter(JoinPoint joinPoint){
        System.out.println("方法执行之后");
        for (int i = 0; i < joinPoint.getArgs().length; i++) {
            System.out.println(joinPoint.getArgs()[i]);
        }
        System.out.println(joinPoint.getSignature().getName());



    }


}
