package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.util.BeanPropertiesUtil;
import com.art1001.supply.util.Stringer;
import com.art1001.supply.util.ValidatorUtils;
import com.art1001.supply.validation.role.RoleIdValidation;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * 角色api
 * @author 汪亚锋
 * [POST]   // 新增
 * [GET]    // 查询
 * [PATCH]  // 更新
 * [PUT]    // 覆盖，全部更新
 * [DELETE] // 删除
 */
@Slf4j
@RestController
@RequestMapping("roles")
@Validated
public class RoleApi {

    @Resource
    private RoleService roleService;

    /**
     * 添加角色
     * @param orgId 企业id
     * @param roleName 角色名称
     * @param roleDes 角色描述
     * @param roleKey 角色标识
     * @return
     */
    @PostMapping("/org")
    public JSONObject addRole(@RequestParam(value = "orgId")String orgId,
                              @RequestParam(value = "roleName")String roleName,
                              @RequestParam(value = "roleDes")String roleDes,
                              @RequestParam(value = "roleKey")String roleKey){
        JSONObject object = new JSONObject();
        try{

            Role role = new Role();
            role.setRoleName(roleName);
            role.setRoleDes(roleDes);
            role.setRoleKey(roleKey);
            role.setOrganizationId(orgId);
            role.setRoleStatus(0);
            role.setCreateTime(new Timestamp(System.currentTimeMillis()));
            role.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            roleService.save(role);
            object.put("result",1);
            object.put("data", role);
            return object;
        }catch(Exception e){
            throw new AjaxException("系统异常,角色添加失败!",e);
        }
    }

    /**
     * 删除角色
     * @param roleId 角色id
     * @return
     */
    @DeleteMapping("/{roleId}/org")
    public JSONObject deleteRole(@PathVariable(value = "roleId")Integer roleId,
                                 @NotEmpty(message = "orgId不能为空") @RequestParam(required = false) String orgId){
        JSONObject object = new JSONObject();
        int result = roleService.removeOrgRole(roleId,orgId);
        object.put("result",1);
        return object;
    }

    /**
     * 更新角色
     * @param roleId 角色id
     * @param roleName 角色名称
     * @param roleDes 角色描述
     * @param roleKey 角色标识
     * @return
     */
    @PutMapping("/{roleId}")
    public JSONObject updateRole(@PathVariable(value = "roleId")Integer roleId,
                                 @RequestParam(value = "roleName",required = false)String roleName,
                                 @RequestParam(value = "roleDes",required = false)String roleDes,
                                 @RequestParam(value = "roleKey",required = false)String roleKey){
        JSONObject object = new JSONObject();
        try{

            Role role = new Role();
            role.setRoleId(roleId);
            role.setRoleName(roleName);
            role.setRoleDes(roleDes);
            role.setRoleKey(roleKey);
            role.setRoleStatus(0);
            role.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            roleService.updateById(role);
            object.put("result",1);
        }catch(Exception e){
            throw new AjaxException("系统异常,角色更新失败!",e);
        }
        return object;
    }

    /**
     * 角色列表
     * @return
     */
    @GetMapping("/{current}/{size}")
    public JSONObject roleList(@RequestParam(value = "roleName",required = false)String roleName,
                               @RequestParam(value = "orgId")String orgId,
                               @PathVariable(value = "current")Long current,
                               @PathVariable(value = "size")Long size){
        JSONObject object = new JSONObject();
        try{
            Role role = new Role();
            role.setRoleName(roleName);
            role.setOrganizationId(orgId);
            Page<Role> roleList = roleService.selectListPage(current, size, role);
            object.put("data",roleList);
            object.put("result",1);
            object.put("msg","查询成功");
        }catch(Exception e){
            log.error("查询失败，{}",e);
            throw new AjaxException(e);
        }
        return object;
    }

    @GetMapping("/{roleId}")
    public JSONObject roleUserList(@PathVariable Integer roleId){
        JSONObject object = new JSONObject();
        try{

            object.put("result",1);
            object.put("msg","");
        }catch(Exception e){
            throw new AjaxException(e);
        }
        return object;
    }

}
