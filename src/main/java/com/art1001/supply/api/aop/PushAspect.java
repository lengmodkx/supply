package com.art1001.supply.api.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.entity.user.UserInfo;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.notice.NoticeService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserNewsService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.ObjectsUtil;
import com.art1001.supply.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.shiro.util.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
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

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * @author shaohua
 */
@Slf4j
@Aspect
@Component
public class PushAspect extends BaseController {

    @Resource
    private NoticeService noticeService;

    @Resource
    private LogService logService;

    @Resource
    private UserNewsService userNewsService;

    @Resource
    private ProjectService projectService;

    @Resource
    private ProjectMemberService projectMemberService;

    @Resource
    private TaskService taskService;

    @Resource
    private UserService userService;

    @Resource
    private RedisUtil redisUtil;

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
            //即需要推送到项目频道也要推送到指定用户频道
        } else if(push.type() == 3){
            noticeService.pushMsg(object.getString("msgId"),push.value().name(),object.get("data"));
            Log log = this.saveLog(object, push);
            if(Constants.TASK.equals(object.getString("publicType"))){
                String[] ids = taskService.getTaskJoinAndExecutorId(object.getString("id"));
                if(ids != null && ids.length > 0){
                    userNewsService.saveUserNews(ids,object.getString("id"),object.getString("publicType"),log.getContent());
                }
            }
            if(Constants.PROJECT.equals(object.getString("publicType"))){
                List<String> ids = projectMemberService.getProjectAllMemberId(object.getString("id"));
                if(!CollectionUtils.isEmpty(ids)){
                    String[] arrIds = new String[ids.size()];
                    noticeService.toUsers(ids.toArray(arrIds), push.value().name(), object.get("data"));
                }

            }
        } else if(push.type() == 4){
            noticeService.pushMsg(object.getString("msgId"),push.value().name(),object.get(push));
        }
        else{//只需要日志
            if(object.containsKey(ID)){
                this.saveLog(object,push);
            }
        }
        //去除无用的返回参数
         object.remove("msgId");
        object.remove("data");
        object.remove("id");
        object.remove("name");
        object.remove("users");
        object.remove("publicType");
    }

    /**
     * 保存操作日志
     * @param object 返回值信息
     */
    private Log saveLog(JSONObject object,Push push){
        Log systemLog = new Log();
        systemLog.setPublicId(object.getString(ID));
        systemLog.setProjectId(object.getString(PROJECT_ID));
        systemLog.setCreateTime(System.currentTimeMillis());
        String name = object.getString(NAME) != null ? object.getString(NAME):"";

        UserInfo userInfo = (UserInfo) redisUtil.getObj(Constants.USER_INFO+ShiroAuthenticationManager.getUserId());
        systemLog.setContent(userInfo.getUserName() + " " + push.value().getName()+" "+ name);
        systemLog.setMemberId(ShiroAuthenticationManager.getUserId());
        logService.save(systemLog);
        return systemLog;
    }


}
