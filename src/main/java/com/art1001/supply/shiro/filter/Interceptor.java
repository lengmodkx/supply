package com.art1001.supply.shiro.filter;

import com.art1001.supply.service.resource.ProResourcesService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Component
public class Interceptor implements HandlerInterceptor {

    @Resource
    private ProResourcesService proResourcesService;

    @Resource
    private RedisUtil redisUtil;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        if(!(handler instanceof HandlerMethod)){
//            return true;
//        }
//
//        List<String> keyList = redisUtil.getList(String.class, "resources:" + ShiroAuthenticationManager.getUserId());
//        if(CollectionUtils.isEmpty(keyList)){
//            keyList = proResourcesService.getMemberResourceKey(request.getParameter("projectId"), ShiroAuthenticationManager.getUserId());
//            redisUtil.lset("resources:" + ShiroAuthenticationManager.getUserId(), keyList);
//        }
//
//        HandlerMethod handlerMethod = (HandlerMethod)handler;
//
//        String name = handlerMethod.getMethod().getName();
//
//        String className = handlerMethod.getBeanType().getSimpleName();
//
//        StringBuilder key = new StringBuilder(className);
//        key.append(":").append(name);
//
//        if(keyList.contains(key.toString())){
//            log.info("用户：{} -- 拥有{}权限", ShiroAuthenticationManager.getUserId(), key);
//            return true;
//        }
        return true;
    }
}
