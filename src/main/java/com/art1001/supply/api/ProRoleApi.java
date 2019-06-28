package com.art1001.supply.api;


import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.entity.role.ProRole;
import com.art1001.supply.service.role.ProRoleService;
import com.art1001.supply.util.NumberUtils;
import com.art1001.supply.util.ValidatorUtils;
import com.art1001.supply.validation.role.AddProRoleValidation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

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
public class ProRoleApi extends BaseController {

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

    /**
     * 删除角色
     * @param roleId 角色id
     * @param projectId 项目id
     * @return 是否成功
     */
    @DeleteMapping
    public JSONObject removeProRole(@Min(value = 0,message = "参数错误!") String roleId,
                                    @NotBlank(message = "项目id不能为空!") String projectId){
        JSONObject jsonObject = new JSONObject();
        if(!NumberUtils.isNumber(roleId)){
            return error("roleId必须为整数!");
        }
        boolean roleNotExist = !proRoleService.checkRoleIsExistByRoleId(Integer.valueOf(roleId));
        if(roleNotExist){
            jsonObject.put("result", 1);
            return jsonObject;
        }
        boolean isSystemInit = proRoleService.checkRoleIsSystemInit(Integer.valueOf(roleId));
        if(isSystemInit){
            jsonObject.put("result", 0);
            jsonObject.put("msg", "不能删除系统初始化的角色!");
            return jsonObject;
        }
        Integer result = proRoleService.removeProRole(Integer.valueOf(roleId),projectId);
        if(result > 0){
            jsonObject.put("result", result);
        } else {
            jsonObject.put("msg","操作失败!");
            jsonObject.put("result", 0);
        }
        return jsonObject;
    }

    /**
     * 更新角色
     * @param roleId 角色id
     * @param roleName 角色名称
     * @return 是否成功
     */
    @PutMapping("/{roleId}/name")
    public JSONObject updateRole(@PathVariable(value = "roleId")String roleId,
                                 @RequestParam(value = "roleName") @NotBlank(message = "角色名称不能为空!") String roleName){
        JSONObject jsonObject = new JSONObject();
        if(!NumberUtils.isNumber(roleId)){
            return error("roleId必须为整数!");
        }
        boolean roleNotExist = proRoleService.checkRoleIsNotExistByRoleId(Integer.valueOf(roleId));
        if(roleNotExist){
           jsonObject.put("msg", "该角色不存在,无法修改!");
           jsonObject.put("result", 0);
        }
        Integer result = proRoleService.updateRoleName(Integer.valueOf(roleId),roleName);
        jsonObject.put("result", result);
        return success(jsonObject);
    }

    @PutMapping("/default")
    public JSONObject updateDefaultRole(String projectId, String roleKey){
        JSONObject jsonObject = new JSONObject();
        proRoleService.setProDefaultRole(projectId, roleKey);
        return jsonObject;
    }

    /**
     * 获取项目角色列表
     * @param projectId 项目角色
     * @return 角色列表
     */
    @GetMapping("/{projectId}")
    public JSONObject getProRoles(@PathVariable String projectId){
        JSONObject jsonObject = new JSONObject();
        List<ProRole> roles = proRoleService.getProRoles(projectId);
        jsonObject.put("result", 1);
        jsonObject.put("data", roles);
        return jsonObject;
    }
}

