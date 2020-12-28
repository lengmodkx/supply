package com.art1001.supply.api;



import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.service.resource.ProResourcesRoleService;
import com.art1001.supply.service.role.ProRoleService;
import com.art1001.supply.util.NumberUtils;

import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;


/**
 * <p>
 * 角色权限映射表 前端控制器
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
@RestController
@Slf4j
@Validated
@RequestMapping("/pro_res_role")
public class ProResourcesRoleApi extends BaseController {

    /**
     * 资源角色业务Bean 注入
     */
    @Resource
    private ProResourcesRoleService proResourcesRoleService;

    /**
     * 项目角色业务bean注入
     */
    @Resource
    private ProRoleService proRoleService;

    /**
     * 分配权限
     * @return 结果oRole
     */
    @PutMapping("/{role}/edit_resource")
    public JSONObject editResource(@PathVariable(value = "role") String roleId,
                                   @RequestParam(value = "resources") @NotEmpty(message = "必须选择至少一个资源!") String resources
                                   ){
        JSONObject jsonObject = new JSONObject();

        if(!NumberUtils.isNumber(roleId)){
            return error("参数roleId必须为整数!");
        }

        if(proRoleService.checkRoleIsNotExistByRoleId(Integer.valueOf(roleId))){
            return error("roleId角色不存在!");
        }

        proResourcesRoleService.distributionRoleResource(Integer.valueOf(roleId), resources);

        jsonObject.put("result",1);
        return jsonObject;
    }

}

