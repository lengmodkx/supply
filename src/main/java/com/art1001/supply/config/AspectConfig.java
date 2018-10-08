package com.art1001.supply.config;

import com.art1001.supply.service.log.LogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@Aspect
public class AspectConfig {

    /** 日志逻辑层接口 */
    @Resource
    private LogService logService;


    @Pointcut("execution(public * com.art1001.supply.api.*.*(..)) && @annotation(com.art1001.supply.annotation.Todo)")
    public void todo(){}

    @Before("todo()")
    public void before(){
        System.out.println("方法执行之钱");
    }

    @AfterReturning(returning = "result", pointcut = "todo()")
    public void doAfter(Object result){


    }


    @After("todo()")
    public void after(JoinPoint jp){

    }




}
