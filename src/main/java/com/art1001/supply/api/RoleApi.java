package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.entity.role.RoleUser;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.resource.ResourceService;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.service.role.RoleUserService;
import com.art1001.supply.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色api
 *
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

    @Resource
    private RoleUserService roleUserService;

    @Resource
    private OrganizationMemberService organizationMemberService;

    @Resource
    private ResourceService resourceService;

    @Resource
    private RedisUtil redisUtil;

    private static final String ROLENAME = "拥有者";

    /**
     * 添加角色
     *
     * @param orgId    企业id
     * @param roleName 角色名称
     * @param roleDes  角色描述
     * @param roleKey  角色标识
     * @return
     */
    @PostMapping("/org")
    public JSONObject addRole(@RequestParam(value = "orgId") String orgId,
                              @RequestParam(value = "roleName") String roleName,
                              @RequestParam(value = "roleDes") String roleDes,
                              @RequestParam(value = "roleKey") String roleKey) {
        JSONObject object = new JSONObject();
        try {
            /*if(roleService.getOrgRoleIdByName(orgId, roleName) > 0) {
                object.put("result", 0);
                object.put("msg", "请检查角色名称是否已存在！");
                return object;
            }
            if(roleService.getOrgRoleIdByKey(orgId, roleKey) > 0) {
                object.put("result", 0);
                object.put("msg", "key已经存在！");
                return object;
            }*/
            Role role = new Role();
            role.setRoleName(roleName);
            role.setRoleDes(roleDes);
            role.setRoleKey(roleKey);
            role.setOrganizationId(orgId);
            role.setRoleStatus(0);
            role.setCreateTime(new Timestamp(System.currentTimeMillis()));
            role.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            roleService.save(role);
            object.put("result", 1);
            object.put("data", role);
            return object;
        } catch (Exception e) {
            throw new AjaxException("系统异常,角色添加失败!", e);
        }
    }

    /**
     * 删除角色
     *
     * @param roleId 角色id
     * @return
     */
    @DeleteMapping("/{roleId}/org")
    public JSONObject deleteRole(@PathVariable(value = "roleId") Integer roleId,
                                 @NotEmpty(message = "orgId不能为空") String orgId,
                                 @RequestParam(value = "memberId") String memberId) {
        JSONObject object = new JSONObject();
        int result = roleService.removeOrgRole(roleId, orgId);
        boolean remove = organizationMemberService.remove(new QueryWrapper<OrganizationMember>().eq("organization_id", orgId).eq("member_Id", memberId));
        if (result == 1 && remove) {
            object.put("result", result);
            object.put("msg", "删除成功");
        } else {
            throw new AjaxException("此角色已经分配给用户，不允许删除");
        }

        return object;
    }

    /**
     * 角色列表
     *
     * @return
     */
    @GetMapping("/{current}/{size}")
    public JSONObject roleList(@RequestParam(value = "roleName", required = false) String roleName,
                               @RequestParam(value = "orgId") String orgId,
                               @PathVariable(value = "current") Long current,
                               @PathVariable(value = "size") Long size) {
        JSONObject object = new JSONObject();
        try {
            Role role = new Role();
            role.setRoleName(roleName);
            role.setOrganizationId(orgId);
            Page<Role> roleList = roleService.selectListPage(current, size, role, orgId);
            object.put("data", roleList);
            object.put("result", 1);
            object.put("msg", "查询成功");
        } catch (Exception e) {
            log.error("查询失败，{}", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新角色
     *
     * @param roleId   角色id
     * @param roleName 角色名称
     * @param roleDes  角色描述
     * @param roleKey  角色标识
     * @return
     */
    @PutMapping("/{roleId}")
    public JSONObject updateRole(@PathVariable(value = "roleId") Integer roleId,
                                 @RequestParam(value = "roleName", required = false) String roleName,
                                 @RequestParam(value = "roleDes", required = false) String roleDes,
                                 @RequestParam(value = "roleKey", required = false) String roleKey) {
        JSONObject object = new JSONObject();
        try {

            Role role = new Role();
            role.setRoleId(roleId);
            role.setRoleName(roleName);
            role.setRoleDes(roleDes);
            role.setRoleKey(roleKey);
            role.setRoleStatus(0);
            role.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            roleService.updateById(role);
            object.put("result", 1);
        } catch (Exception e) {
            throw new AjaxException("系统异常,角色更新失败!", e);
        }
        return object;
    }

    /**
     * 企业成员角色列表
     *
     * @return
     */
    @GetMapping
    public JSONObject roleList(@RequestParam(value = "orgId") String orgId,
                               @RequestParam(value = "userId") String userId) {
        JSONObject object = new JSONObject();
        try {
            object.put("data", roleService.roleForMember(userId, orgId));
            object.put("result", 1);
            object.put("msg", "查询成功");
        } catch (Exception e) {
            log.error("查询失败，{}", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新企业成员角色
     *
     * @param roleId
     * @return
     */
    @PutMapping("/{roleId}/update")
    public JSONObject updateUserRole(@PathVariable(value = "roleId") Integer roleId,
                                     @RequestParam(value = "orgId") String orgId,
                                     @RequestParam(value = "userId") String userId) {
        JSONObject object = new JSONObject();
        try {

            RoleUser roleUser = roleUserService.getOne(new QueryWrapper<RoleUser>().eq("u_id", userId).eq("org_id", orgId));
           //弥补之前疏忽，企业成员未添加到角色表的数据需要重新添加
            if (roleUser==null) {
                RoleUser roleUser1 = new RoleUser();
                roleUser1.setUId(userId);
                roleUser1.setOrgId(orgId);
                roleUser1.setRoleId(roleId);
                roleUser1.setTCreateTime(LocalDateTime.now());
                roleUserService.save(roleUser1);
            }else {
                roleUser.setUId(userId);
                roleUser.setOrgId(orgId);
                roleUser.setRoleId(roleId);
                roleUserService.updateById(roleUser);
            }
            Role role = roleService.getOne(new QueryWrapper<Role>().eq("role_id", roleId));
            OrganizationMember orgm=new OrganizationMember();
            orgm.setMemberLabel(role.getRoleName());
            organizationMemberService.update(orgm,new QueryWrapper<OrganizationMember>().eq("organization_id",orgId).eq("member_id",userId));

            List<String> memberResourceKey = resourceService.getMemberResourceKey(userId, orgId);
            redisUtil.remove("orgms:" + userId);
            redisUtil.lset("orgms:"+userId, memberResourceKey);
            object.put("result", 1);
            object.put("msg", "更新成功");

        } catch (Exception e) {
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 更新企业的默认角色
     *
     * @param orgId  企业id
     * @param roleId 角色id
     * @return 是否成功
     */
    @PutMapping("/{orgId}/{roleId}/org_default_role")
    public JSONObject updateOrgDefaultRole(@NotEmpty(message = "orgId 不能为空!") @PathVariable String orgId,
                                           @NotEmpty(message = "roleId不能为空!") @PathVariable String roleId) {
        JSONObject jsonObject = new JSONObject();
        int result = roleService.updateOrgDefaultRole(orgId, roleId);
        jsonObject.put("result", result);
        return jsonObject;
    }

}
