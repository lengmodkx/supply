package com.art1001.supply.api;


import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.role.RoleUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 用户角色映射表 前端控制器
 * </p>
 *
 * @author heshaohua
 * @since 2019-05-28
 */
@RestController
@RequestMapping("/role_user")
public class RoleUserApi {

    @Resource
    private RoleUserService roleUserService;

    @Resource
    private OrganizationService  organizationService;

    /**
     * 判断是否是拥有者
     * @param orgId
     * @return
     */
    @GetMapping("/isOwner/{orgId}")
    public JSONObject isOwner(@PathVariable(value = "orgId") String orgId){
        JSONObject jsonObject = new JSONObject();
        try {
            Integer result = roleUserService.isOwner(orgId);
            Organization byId = organizationService.getById(orgId);
            jsonObject.put("result",1);
            jsonObject.put("msg",result);
            jsonObject.put("data",byId);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }
}

