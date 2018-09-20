package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.role.RoleEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.role.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setName(roleName);
            roleEntity.setDescription(roleDes);
            roleEntity.setKey(roleKey);
            roleEntity.setOrgId(orgId);
            roleService.insert(roleEntity);
            object.put("result",1);
            object.put("msg","插入失败");
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
    public JSONObject deleteRole(@PathVariable(value = "roleId")Long roleId){
        JSONObject object = new JSONObject();
        try{
            roleService.deleteRoleById(roleId);
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
    public JSONObject updateRole(@PathVariable(value = "roleId")Long roleId,
                                 @RequestParam(value = "roleName")String roleName,
                                 @RequestParam(value = "roleDes")String roleDes,
                                 @RequestParam(value = "roleKey")String roleKey){
        JSONObject object = new JSONObject();
        try{
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setName(roleName);
            roleEntity.setDescription(roleDes);
            roleEntity.setKey(roleKey);
            roleEntity.setId(roleId);
            roleService.update(roleEntity);
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
    @GetMapping
    public JSONObject roleList(){
        JSONObject object = new JSONObject();
        try{
            Map<String,Object> map = new HashMap<>();
            roleService.queryListByPage(map);
            object.put("result",1);
            object.put("msg","查询成功");
        }catch(Exception e){
            log.error("查询失败，{}",e);
            throw new AjaxException(e);
        }
        return object;
    }
}
