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
import jodd.util.HashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Hash;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        String userId = ShiroAuthenticationManager.getUserId();
        List<String> keyList = new ArrayList<>();
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
            String orgByUserId = organizationMemberService.findOrgByUserId(userId);
            List<String> list = redisUtil.getList(String.class, "perms:" + userId);
            if(CollectionUtils.isEmpty(list)){
                list = resourceService.getMemberResourceKey(userId, orgByUserId);
                redisUtil.lset("perms:" + userId, list);
            }
            if(list.contains(key.toString())){
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
            writer.flush();
            return false;
        }
        return true;
    }
}
