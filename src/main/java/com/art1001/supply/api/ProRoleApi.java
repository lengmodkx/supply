package com.art1001.supply.api;


import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.common.AuthToRedis;
import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.role.ProRole;
import com.art1001.supply.service.role.ProRoleService;
import com.art1001.supply.util.NumberUtils;
import com.art1001.supply.util.ValidatorUtils;
import com.art1001.supply.validation.role.AddProRoleValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
@Slf4j
@RestController
@RequestMapping("/pro_role")
public class ProRoleApi extends BaseController {

    /**
     * 注入项目角色业务层Bean
     */
    @Resource
    private ProRoleService proRoleService;

    @Resource
    private AuthToRedis authToRedis;

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
            jsonObject.put("result", 1);
            jsonObject.put("msg", "项目角色添加成功!");
            jsonObject.put("data",proRole);
        } else {
            jsonObject.put("result", 0);
            jsonObject.put("msg", "添加失败,角色key在该项目中已存在!");
        }
        return jsonObject;
    }

    /**
     * 删除角色
     * @param roleId 角色id
     * @param orgId 项目id
     * @return 是否成功
     */
    @DeleteMapping("/{roleId}")
    public JSONObject removeProRole(@PathVariable @Min(value = 0,message = "参数错误!") String roleId,
                                    @NotBlank(message = "企业id不能为空!") String orgId){
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
        Integer result = proRoleService.removeProRole(Integer.valueOf(roleId),orgId);
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
    @PutMapping("/{roleId}")
    public JSONObject updateRole(@PathVariable(value = "roleId")String roleId,
                                 String roleName,
                                 String roleDes,
                                 String roleKey){
        JSONObject jsonObject = new JSONObject();
        if(!NumberUtils.isNumber(roleId)){
            return error("roleId必须为整数!");
        }
        boolean roleNotExist = proRoleService.checkRoleIsNotExistByRoleId(Integer.valueOf(roleId));
        if(roleNotExist){
           jsonObject.put("msg", "该角色不存在,无法修改!");
           jsonObject.put("result", 0);
        }
        ProRole proRole = new ProRole();
        proRole.setRoleId(Integer.valueOf(roleId));
        proRole.setRoleName(roleName);
        proRole.setRoleDes(roleDes);
        proRole.setRoleKey(roleKey);
        proRole.setUpdateTime(LocalDateTime.now());

        proRoleService.updateById(proRole);
        jsonObject.put("result", 1);
        return success(jsonObject);
    }

    /**
     * 设置项目的默认角色
     * @param orgId 企业id
     * @param roleKey 默认角色的角色key
     */
    @PutMapping("/default")
    public JSONObject updateDefaultRole(String orgId, String roleKey){
        JSONObject jsonObject = new JSONObject();
        proRoleService.setProDefaultRole(orgId, roleKey);
        jsonObject.put("result",1);
        return jsonObject;
    }

    /**
     * 获取项目角色列表
     * @param orgId 项目角色
     * @return 角色列表
     */
    @GetMapping("/{orgId}")
    public JSONObject getProRoles(@PathVariable String orgId){
        JSONObject jsonObject = new JSONObject();
        List<ProRole> roles = proRoleService.getProRoles(orgId);
        jsonObject.put("result", 1);
        jsonObject.put("data", roles);
        return jsonObject;
    }

    @GetMapping("/for_member")
    public Result roleForMember(@NotNull(message = "用户id不能为空！") String userId,
                                @NotNull(message = "企业id不能为空！") String orgId,
                                @NotNull(message = "项目id不能为空！") String projectId){
        log.info("Get role for member.[{}]", userId);

        return Result.success(proRoleService.roleForMember(userId, orgId,projectId));
    }
}

