package com.art1001.supply.shiro.filter;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.resource.ProResources;
import com.art1001.supply.entity.resource.ResourceEntity;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.resource.ProResourcesService;
import com.art1001.supply.service.resource.ResourceService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shaohua
 */
@Slf4j
@Component("interceptor")
public class Interceptor implements HandlerInterceptor {

    @Resource
    private ProResourcesService proResourcesService;

    @Resource
    private ResourceService resourceService;

    @Resource
    private OrganizationMemberService organizationMemberService;

    @Resource
    private RedisUtil redisUtil;



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        List<String> keyList = new ArrayList<>();
        keyList.add("ScheduleApi:initSchedule");
        keyList.add("ShareApi:share");
        keyList.add("StatisticsApi:projectStatistics");
        keyList.add("ShareApi:getShare");
        keyList.add("ScheduleApi:getSchedule");
        HandlerMethod handlerMethod = (HandlerMethod)handler;
        String name = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBeanType().getSimpleName();
        // 当前请求的资源路径
        StringBuilder key = new StringBuilder(className);
        key.append(":").append(name);
        // 获悉系统所有资源
        List<String> allResources = redisUtil.getList(String.class, "allResources");

        // 所有资源包含当前请求路径
        if(allResources.contains(key.toString())){
            String userId = ShiroAuthenticationManager.getUserId();
            String orgByUserId = organizationMemberService.findOrgByUserId(userId);
            // 获取当前登录人的企业权限
            List<String> list = redisUtil.getList(String.class, "orgms:" + userId);
            // 为空，第一次登陆
            if(CollectionUtils.isEmpty(list)){
                // 查询出本企业下当前登录人所拥有的所有权限并存入redis
                list = resourceService.getMemberResourceKey(userId, orgByUserId);
                redisUtil.lset("orgms:" + userId, list);
            }

            // 获取当前登录人的项目权限
            List<String> permsList = redisUtil.getList(String.class, "perms:" + userId);
            // 若企业权限或项目权限都包含当前登录人的资源请求路径则通过
            if(list.contains(key.toString()) || permsList.contains(key.toString())){
                log.info("用户：{} -- 拥有{}权限", ShiroAuthenticationManager.getUserId(), key);
                return true;
            }
            log.info("用户：{} -- 无{}权限", ShiroAuthenticationManager.getUserId(), key);
            response.setStatus(203);
            response.setContentType("application/json; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg","您没有权限!");
            jsonObject.put("result",203);
            PrintWriter writer = response.getWriter();
            writer.print(jsonObject.toJSONString());
            writer.close();
            return false;
        }
        return true;
    }
}
