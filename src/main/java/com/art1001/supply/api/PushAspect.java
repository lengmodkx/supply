package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.notice.NoticeService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import org.aspectj.lang.JoinPoint;
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
        //先写入操作日志
        Push push = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(Push.class);
        //只需要推送，不需要日志
        if(push.type()==0){
            noticeService.pushMsg(object.getString("msgId"),push.value().name(),object.get("data"));
        } else if (push.type()==1){ //既需要推送也需要日志
            noticeService.pushMsg(object.getString("msgId"),push.value().name(),object.get("data"));
            Log log = new Log();
            log.setPublicId(object.getString("id"));
            log.setProjectId(object.getString("msgId"));
            log.setCreateTime(System.currentTimeMillis());
            log.setContent(ShiroAuthenticationManager.getUserEntity().getUserName() + " " + push.value().getName()+" "+object.getString("name"));
            log.setMemberId(ShiroAuthenticationManager.getUserId());
            logService.save(log);
        }else{//只需要日志
            if(object.containsKey("id")){
                Log log = new Log();
                log.setPublicId(object.getString("id"));
                log.setProjectId(object.getString("msgId"));
                log.setCreateTime(System.currentTimeMillis());
                log.setContent(ShiroAuthenticationManager.getUserEntity().getUserName() + " " + push.value().getName()+" "+object.getString("name"));
                log.setMemberId(ShiroAuthenticationManager.getUserId());
                //logService.save(log);
            }
        }

        object.remove("msgId");
        object.remove("data");
        object.remove("id");
        object.remove("name");
    }
}
