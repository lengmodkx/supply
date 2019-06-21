package com.art1001.supply.api;


import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.role.ProRole;
import com.art1001.supply.service.role.ProRoleService;
import com.art1001.supply.util.ValidatorUtils;
import com.art1001.supply.validation.role.AddProRoleValidation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
@Validated
@RestController
@RequestMapping("/pro_role")
public class ProRoleApi {

    /**
     * 注入项目角色业务层Bean
     */
    @Resource
    private ProRoleService proRoleService;

    /**
     * 添加项目角色
     * @param proRole 角色信息
     * @return 结果
     */
    @PostMapping
    public JSONObject addProRole(ProRole proRole){
        ValidatorUtils.validateEntity(proRole, AddProRoleValidation.class);
        JSONObject jsonObject = new JSONObject();
        Integer result = proRoleService.addProRole(proRole);
        if(result > 0){
            jsonObject.put("result", result);
            jsonObject.put("msg", "项目角色添加成功!");
        } else {
            jsonObject.put("result", result);
            jsonObject.put("msg", "添加失败,角色key在该项目中已存在!");
        }
        return jsonObject;
    }

    @DeleteMapping
    public JSONObject removeProRole(@Min(value = 0,message = "参数错误!") Integer roleId,
                                    @NotBlank(message = "项目id不能为空!") String projectId){
        JSONObject jsonObject = new JSONObject();
        boolean roleNotExist = !proRoleService.checkRoleIsExistByRoleId(roleId);
        if(roleNotExist){
            jsonObject.put("result", 1);
            return jsonObject;
        }
        boolean isSystemInit = proRoleService.checkRoleIsSystemInit(roleId);
        if(isSystemInit){
            jsonObject.put("result", 0);
            jsonObject.put("msg", "不能删除系统初始化的角色!");
            return jsonObject;
        }
        Integer result = proRoleService.removeProRole(roleId,projectId);
        if(result > 0){
            jsonObject.put("result", result);
        } else {
            jsonObject.put("msg","操作失败!");
            jsonObject.put("result", 0);
        }
        return jsonObject;
    }
}

