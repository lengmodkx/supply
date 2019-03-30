package com.art1001.supply.api.aop;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.api.base.BaseController;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author shaohua
 */
@Aspect
@Component
public class PushAspect extends BaseController {

    @Resource
    private NoticeService noticeService;

    @Resource
    private LogService logService;

    private final static String ID = "id";
    private final static String PROJECT_ID = "projectId";
    private final static String NAME = "name";
    private final static String SEPARATOR = ",";

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
            //既需要推送也需要日志
        } else if (push.type()==1){
            noticeService.pushMsg(object.getString("msgId"),push.value().name(),object.get("data"));
            this.saveLog(object,push);
            //需要往不同的频道推送不同的数据
        } else if(push.type() == 2){
            Map<String,Object> data = object.getObject("data", HashMap.class);
            data.keySet().forEach(key -> {
                noticeService.pushMsg(key,push.value().name(),data.get(key));
            });
            this.saveLog(object,push);
        } else{//只需要日志
            if(object.containsKey(ID)){
                this.saveLog(object,push);
            }
        }
        //去除无用的返回参数
        object.remove("msgId");
        object.remove("data");
        object.remove("id");
        object.remove("name");
    }

    /**
     * 保存操作日志
     * @param object 返回值信息
     */
    private void saveLog(JSONObject object,Push push){
        Log log = new Log();
        log.setPublicId(object.getString(ID));
        log.setProjectId(object.getString(PROJECT_ID));
        log.setCreateTime(System.currentTimeMillis());
        log.setContent(ShiroAuthenticationManager.getUserEntity().getUserName() + " " + push.value().getName()+" "+object.getString(NAME));
        log.setMemberId(ShiroAuthenticationManager.getUserId());
        logService.save(log);
    }
}
