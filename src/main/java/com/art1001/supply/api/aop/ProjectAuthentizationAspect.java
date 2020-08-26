package com.art1001.supply.api.aop;

import com.art1001.supply.annotation.ProjectAuth;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.role.ProRole;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.role.ProRoleUserService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.ObjectsUtil;
import com.art1001.supply.util.RedisUtil;
import com.art1001.supply.util.SpelParser;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @Description 项目权限授权
 * @Date:2019/6/18 16:50
 * @Author heshaohua
 **/
@Aspect
@Component
@Slf4j
public class ProjectAuthentizationAspect {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ProRoleUserService proRoleUserService;

    @Resource
    private TaskService taskService;

    /**
     * 需要鉴权的请求切点
     */
//    @Pointcut("@annotation(com.art1001.supply.annotation.ProjectAuth)")
//    public void checkPermission(){}

    @Before("@annotation(projectAuth)")
    public void doBefore(JoinPoint joinPoint,ProjectAuth projectAuth){
        String key = getKey(projectAuth.value(),joinPoint);
        String userId = ShiroAuthenticationManager.getUserId();
        String projecId = redisUtil.get("userId:" + userId);
        ProRole role = proRoleUserService.getRole(projecId, userId);
        taskService.getOne(new QueryWrapper<Task>().eq("task_id",key));
//        if(StringUtils.equalsIgnoreCase("administrator",roleKey)){
//
//        }
//
//        List<String> permsList =redisUtil.getList(String.class,"perms:"+userId);
//        boolean notPermission = !permsList.contains(permission);
//        if(notPermission){
//            StringBuilder errMsg = new StringBuilder();
//            errMsg.append("用户:")
//                    .append(ShiroAuthenticationManager.getUserId())
//                    .append("没有权限执行此操作(")
//                    .append(targetMethodName)
//                    .append(")!");
//            log.error(errMsg.toString());
//            throw new AjaxException("无权做此操作，请联系管理进行授权");
//        }
    }

    private String getKey(String key, JoinPoint joinPoint) {
        Method method = ((MethodSignature)(joinPoint.getSignature())).getMethod();
        ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();
        String[] parameterNames = pnd.getParameterNames(method);
        return SpelParser.getKey(key, parameterNames, joinPoint.getArgs());
    }
}
