package com.art1001.supply.shiro.filter;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.resource.ResourceEntity;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.resource.ResourceService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author heshaohua
 */
@Slf4j
@Component("orgInterceptor")
public class OrgInterceptor extends Interceptor{

    @Resource
    private OrganizationMemberService organizationMemberService;

    @Resource
    private ResourceService resourceService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod)){
            return true;
        }


        String defaultOrgId = organizationMemberService.findOrgByUserId(ShiroAuthenticationManager.getUserId());
        List<String> keyList = resourceService.getMemberResourceKey(ShiroAuthenticationManager.getUserId(), defaultOrgId);

        HandlerMethod handlerMethod = (HandlerMethod)handler;

        String name = handlerMethod.getMethod().getName();

        String className = handlerMethod.getBeanType().getSimpleName();

        StringBuilder key = new StringBuilder(className);
        key.append(":").append(name);

        //获取所有资源的kay
        List<ResourceEntity> allResourceList = resourceService.list(null);
        List<String> allKeyList = allResourceList.stream().map(ResourceEntity::getResourceKey).collect(Collectors.toList());

        if(!allKeyList.contains(key.toString())){
            return true;
        }


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
}
