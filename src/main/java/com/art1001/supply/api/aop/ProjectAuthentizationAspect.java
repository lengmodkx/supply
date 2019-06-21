package com.art1001.supply.api.aop;

import com.art1001.supply.annotation.ProAuthentization;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.resource.ProResourcesService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.RedisUtil;
import com.art1001.supply.util.Stringer;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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

    /**
     * 注入项目资源业务层Bean
     */
    @Resource
    private ProResourcesService proResourcesService;

    /**
     * 注入项目成员关系业务层Bean
     */
    @Resource
    private ProjectMemberService projectMemberService;

    /**
     * 需要鉴权的请求切点
     */
    @Pointcut("@annotation(com.art1001.supply.annotation.ProAuthentization)")
    public void push(){}

    @Before("push()")
    public void doBefore(JoinPoint joinPoint){
        String targetMethodName = joinPoint.getSignature().getName();
        ProAuthentization annotation = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(ProAuthentization.class);
        String permission = annotation.value();
        String redisKey = Constants.PRO_SOURCES_PREFIX + ShiroAuthenticationManager.getUserId();
        List<String> list;
        if(redisUtil.exists(redisKey)){
            list = redisUtil.getList(String.class, Constants.PRO_SOURCES_PREFIX + ShiroAuthenticationManager.getUserId());
        } else {
            String userCurrentProjectId = projectMemberService.getUserCurrentProjectId();
            list = proResourcesService.getMemberResourceKey(userCurrentProjectId, ShiroAuthenticationManager.getUserId());
            redisUtil.lset(Constants.PRO_SOURCES_PREFIX + ShiroAuthenticationManager.getUserId(),list);
        }
        boolean notPermission = !list.contains(permission);
        if(notPermission){
            StringBuilder errMsg = new StringBuilder();
            errMsg.append("用户:")
                    .append(ShiroAuthenticationManager.getUserId())
                    .append("没有权限执行此操作(")
                    .append(targetMethodName)
                    .append(")!");
            log.error(errMsg.toString());
            throw new AuthorizationException(errMsg.toString());
        }
    }
}
