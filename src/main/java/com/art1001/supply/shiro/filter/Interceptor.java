package com.art1001.supply.shiro.filter;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.resource.ProResources;
import com.art1001.supply.entity.resource.ResourceEntity;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.resource.ProResourcesService;
import com.art1001.supply.service.resource.ResourceService;
import com.art1001.supply.service.role.ProRoleUserService;
import com.art1001.supply.service.role.ResourcesRoleService;
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
    private ProRoleUserService proRoleUserService;

    @Resource
    private ResourceService resourceService;

    @Resource
    private OrganizationMemberService organizationMemberService;

    @Resource
    private ResourcesRoleService resourcesRoleService;

    @Resource
    private RedisUtil redisUtil;



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        String defaultOrgId = organizationMemberService.findOrgByUserId(ShiroAuthenticationManager.getUserId());
        List<String> keyList = proResourcesService.getMemberResourceKey(defaultOrgId, ShiroAuthenticationManager.getUserId());
        List<String> memberResourceKey = resourceService.getMemberResourceKey(ShiroAuthenticationManager.getUserId(), defaultOrgId);
        keyList.addAll(memberResourceKey);
        keyList.add("ScheduleApi:initSchedule");
        keyList.add("ShareApi:share");
        keyList.add("StatisticsApi:projectStatistics");
        keyList.add("ShareApi:getShare");
        keyList.add("ScheduleApi:getSchedule");
        HandlerMethod handlerMethod = (HandlerMethod)handler;

        String name = handlerMethod.getMethod().getName();

        String className = handlerMethod.getBeanType().getSimpleName();

        StringBuilder key = new StringBuilder(className);
        key.append(":").append(name);

        List<String> allResources = redisUtil.getList(String.class, "allResources");
        if(allResources.contains(key.toString())){
            if(keyList.contains(key.toString())){
                log.info("用户：{} -- 拥有{}权限", ShiroAuthenticationManager.getUserId(), key);
                return true;
            }
            log.info("用户：{} -- 无{}权限", ShiroAuthenticationManager.getUserId(), key);
            response.setStatus(203);
            response.setContentType("application/json; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg","您没有权限!");
            PrintWriter writer = response.getWriter();
            writer.print(jsonObject.toJSONString());
            writer.flush();
            return false;
        }
        return true;
    }
}
