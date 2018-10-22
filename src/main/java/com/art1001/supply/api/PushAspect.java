package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.notice.NoticeService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Aspect
@Component
public class PushAspect {

    @Resource
    private NoticeService noticeService;

    @Resource
    private LogService logService;

    /**
     * 推送的切点
     */
    @Pointcut("@annotation(com.art1001.supply.annotation.Push)")
    public void push(){}

    /**
     * 所有需要推送数据的添加方法
     * @param object 方法返回值
     */
    @AfterReturning(returning = "object", pointcut = "push()")
    public void pushAfter(JoinPoint joinPoint,JSONObject object){
        if(StringUtils.isNotEmpty(object.getString("msgId"))){
            Push push = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(Push.class);
            noticeService.pushMsg(object.getString("msgId"),push.value().getId(),object.getJSONObject("data"));
            Log log = new Log();
            log.setPublicId(object.getString("id"));
            log.setProjectId(object.getString("msgId"));
            log.setLogType(0);
            logService.save(log);
            object.remove("msgId");
            object.remove("data");
            object.remove("id");
        }
    }

    @After("push()")
    public void doAfter(JoinPoint joinPoint){
        System.out.println("方法执行之后");
//        for (int i = 0; i < joinPoint.getArgs().length; i++) {
//            System.out.println(joinPoint.getArgs()[i]);
//        }
//        System.out.println(joinPoint.getSignature().getName());
    }
}
