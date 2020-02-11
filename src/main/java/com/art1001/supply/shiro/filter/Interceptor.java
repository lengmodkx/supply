package com.art1001.supply.shiro.filter;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.resource.ProResourcesService;
import com.art1001.supply.service.role.ProRoleUserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

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
    private OrganizationMemberService organizationMemberService;



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        String defaultOrgId = organizationMemberService.findOrgByUserId(ShiroAuthenticationManager.getUserId());
        List<String> keyList = proResourcesService.getMemberResourceKey(defaultOrgId, ShiroAuthenticationManager.getUserId());

        HandlerMethod handlerMethod = (HandlerMethod)handler;

        String name = handlerMethod.getMethod().getName();

        String className = handlerMethod.getBeanType().getSimpleName();

        StringBuilder key = new StringBuilder(className);
        key.append(":").append(name);

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
