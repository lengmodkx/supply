package com.art1001.supply.config;

import com.art1001.supply.service.log.LogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Slf4j
@Configuration
@Aspect
public class AspectConfig {

    /** 日志逻辑层接口 */
    @Resource
    private LogService logService;


    @Pointcut("execution(public * com.art1001.supply.api.*.*(..)) && @annotation(com.art1001.supply.annotation.Todo)")
    public void todo(){}

    @Before("todo()")
    public void before(JoinPoint joinPoint){
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
        log.info("请求目标 URL : " + request.getRequestURL().toString());
        log.info("请求方式 HTTP_METHOD : " + request.getMethod());
        log.info("客户端 IP : " + request.getRemoteAddr());
        log.info("执行的方法 CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        log.info("附带参数 ARGS : " + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(returning = "result", pointcut = "todo()")
    public void doAfter(Object result){


    }


    @After("todo()")
    public void after(JoinPoint jp){

    }




}
