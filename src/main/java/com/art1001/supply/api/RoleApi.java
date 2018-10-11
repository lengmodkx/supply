package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.role.RoleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
    @PostMapping
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
            object.put("msg","插入成功");
        }catch(Exception e){
            log.error("插入失败，{}",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 删除角色
     * @param roleId 角色id
     * @return
     */
    @DeleteMapping("/{roleId}")
    public JSONObject deleteRole(@PathVariable(value = "roleId")Integer roleId){
        JSONObject object = new JSONObject();
        try{
            roleService.removeById(roleId);
            object.put("result",1);
            object.put("msg","删除成功");
        }catch(Exception e){
            log.error("删除失败，{}",e);
            throw new AjaxException(e);
        }
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
            role.setId(roleId);
            role.setRoleName(roleName);
            role.setRoleDes(roleDes);
            role.setRoleKey(roleKey);
            role.setRoleStatus(0);
            role.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            roleService.updateById(role);
            object.put("result",1);
            object.put("msg","更新成功");
        }catch(Exception e){
            log.error("更新失败，{}",e);
            throw new AjaxException(e);
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
