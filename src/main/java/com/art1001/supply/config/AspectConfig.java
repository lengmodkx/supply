package com.art1001.supply.config;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Todo;
import com.art1001.supply.entity.system.SystemLog;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.system.SystemLogService;
import com.art1001.supply.util.IdGen;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
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
    private SystemLogService systemLogService;


    @Pointcut("@annotation(com.art1001.supply.annotation.Todo)")
    public void todo(){}

    /**
     * 推送的切点
     */
    @Pointcut("@annotation(com.art1001.supply.annotation.MessagePush)")
    public void push(){}

    /**
     * 统计方法执行耗时Around环绕通知
     * @param joinPoint
     * @return
     */
    @Around("todo()")
    public Object timeAround(ProceedingJoinPoint joinPoint) throws Exception {
        Todo todo = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(Todo.class);
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
        log.info("请求目标 URL : " + request.getRequestURL().toString());
        log.info("请求方式 HTTP_METHOD : " + request.getMethod());
        log.info("客户端 IP : " + request.getRemoteAddr());
        log.info("执行的方法 CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        log.info("附带参数 ARGS : " + Arrays.toString(joinPoint.getArgs()));
        log.info("方法备注 : "+ todo.note());

        //封装日志信息
        SystemLog sl = new SystemLog();
        sl.setId(IdGen.uuid());
        sl.setRequestUrl(request.getRequestURL().toString());
        sl.setIp(request.getRemoteAddr());
        sl.setMethodName(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        sl.setRequestMethod(request.getMethod());
        sl.setMethodArgs(Arrays.toString(joinPoint.getArgs()));
        sl.setNote(todo.note());

        // 定义返回对象、得到方法需要的参数
        Object obj = null;
        long startTime = System.currentTimeMillis();
        try {
            obj = joinPoint.proceed(joinPoint.getArgs());
        } catch (Throwable e) {
            log.error("方法执行出错:", e);
            sl.setRunResult("失败:\t" + e.getMessage());
            //异常时存储日志信息
            int result = systemLogService.save(sl);
            throw new AjaxException(e);
        }
        // 获取执行的方法名
        long endTime = System.currentTimeMillis();
        long total = endTime - startTime;
        sl.setRunTime(total+"ms");
        sl.setRunResult("成功");
        //存储日志信息
        int result = systemLogService.save(sl);

        log.info("方法执行时间:\t"+ total + "ms");
        return obj;
    }

    /**
     * 推送走这里
     * @param object 方法返回值
     */
    @AfterReturning(returning = "object", pointcut = "push()")
    public void pushAfter(JSONObject object){

    }

    //后置异常通知
    @AfterThrowing("todo()")
    public void throwss(JoinPoint jp){
        log.error("方法执行异常");
    }

    @After("todo()")
    public void doAfter(JoinPoint joinPoint){
        System.out.println("方法执行之后");
        for (int i = 0; i < joinPoint.getArgs().length; i++) {
            System.out.println(joinPoint.getArgs()[i]);
        }
        System.out.println(joinPoint.getSignature().getName());
    }




}
