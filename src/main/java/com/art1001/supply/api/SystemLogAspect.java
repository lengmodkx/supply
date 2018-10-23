package com.art1001.supply.api;

import com.art1001.supply.annotation.Log;
import com.art1001.supply.entity.system.SystemLog;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.system.SystemLogService;
import com.art1001.supply.util.IdGen;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
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
public class SystemLogAspect {

    /** 日志逻辑层接口 */
    @Resource
    private SystemLogService systemLogService;


    @Pointcut("@annotation(com.art1001.supply.annotation.Log)")
    public void todo(){}

    /**
     * 统计方法执行耗时Around环绕通知
     * @param joinPoint
     * @return
     */
    @Around("todo()")
    public Object timeAround(ProceedingJoinPoint joinPoint) throws Exception {
        Log todo = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(Log.class);
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //封装日志信息
        SystemLog sl = new SystemLog();
        sl.setId(IdGen.uuid());
        sl.setRequestUrl(request.getRequestURL().toString());
        sl.setIp(request.getRemoteAddr());
        sl.setMethodName(joinPoint.getSignature().getName());
        sl.setRequestMethod(request.getMethod());
        sl.setMethodArgs(Arrays.toString(joinPoint.getArgs()));
        sl.setNote(todo.value().getName());

        // 定义返回对象、得到方法需要的参数
        Object obj = null;
        long startTime = System.currentTimeMillis();
        try {
            obj = joinPoint.proceed(joinPoint.getArgs());
        } catch (Throwable e) {
            sl.setRunResult("失败:\t" + e.getMessage());
            //异常时存储日志信息
            systemLogService.save(sl);
            throw new AjaxException(e);
        }
        long endTime = System.currentTimeMillis();
        long total = endTime - startTime;
        sl.setRunTime(total+"ms");
        sl.setRunResult("成功");
        //存储日志信息
        systemLogService.save(sl);
        return obj;
    }
}
