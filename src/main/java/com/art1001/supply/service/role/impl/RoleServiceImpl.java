package com.art1001.supply.service.role.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.role.ProRole;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.entity.role.RoleUser;
import com.art1001.supply.mapper.role.RoleMapper;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.role.ResourcesRoleService;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.service.role.RoleUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author heshaohua
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleUserService roleUserService;

    @Resource
    private OrganizationService organizationService;

    @Resource
    private ResourcesRoleService resourcesRoleService;

    @Resource
    private OrganizationMemberService organizationMemberService;

    private static final String ONE = "成员";

    @Override
    public Page<Role> selectListPage(long current, long size, Role role, String orgId) {
        Page<Role> rolePage = new Page<>(current, size);
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        if (role.getRoleName() == null) {
            queryWrapper = new QueryWrapper<Role>().eq("organization_id", role.getOrganizationId());
        } else {
            queryWrapper = new QueryWrapper<Role>()
                    .and(roleQueryWrapper -> roleQueryWrapper
                            .eq("role_name", role.getRoleName())
                            .or(true).eq("organization_id", "0"))
                    .eq("organization_id", role.getOrganizationId());
        }
        return (Page<Role>) page(rolePage, queryWrapper);
    }

    /**
     * 判断角色是否存在
     *
     * @param roleId 角色id
     * @return 结果
     * @author heShaoHua
     * @describe 暂无
     * @updateInfo 暂无
     * @date 2019/5/27
     */
    @Override
    public Boolean checkIsExist(String roleId) {
        return roleMapper.selectCount(new QueryWrapper<Role>().lambda().eq(Role::getRoleId, roleId)) > 0;
    }

    /**
     * 移除企业角色
     * 1.查询出这个角色对应的用户信息
     * 2.将查询出来的用户信息的对应角色设置为该企业的默认角色
     * 3.如果当前删除的角色信息正是该企业的默认角色那么删除后就要将该企业的 "成员角色" 设置为默认角色
     *
     * @param roleId 角色id
     * @return 是否移除成功
     * @author heShaoHua
     * @describe 暂无
     * @updateInfo 暂无
     * @date 2019/5/28 12:06
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeOrgRole(Integer roleId, String orgId) {
        //企业下角色关联有用户，不允许删除
        List<RoleUser> roleUsers = roleUserService.list(new QueryWrapper<RoleUser>().eq("role_id", roleId).eq("org_id", orgId));
        if (roleUsers != null && roleUsers.size() > 0) {
            return 0;
        }

        //删除角色
        return roleMapper.deleteById(roleId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer saveOrgDefaultRole(String orgId) {

        if (!organizationService.checkOrgIsExist(orgId)) {
            return -1;
        }
        //这里手动添加默认角色信息,后期可以读取配置文件进行添加
        String[] initRoles = {"拥有者", "管理员", "成员", "外部成员"};
        for (String roleName : initRoles) {
            Role role = new Role();
            role.setRoleName(roleName);
            switch (roleName) {
                case Constants.OWNER_CN:
                    role.setRoleKey("administrator");
                    role.setRoleDes("企业的拥有者");
                    break;
                case Constants.ADMIN_CN:
                    role.setRoleKey("admin");
                    role.setRoleDes("企业的管理者");
                    break;
                case Constants.EXTERNAL:
                    role.setRoleKey("externalMember");
                    role.setRoleDes("企业外部成员");
                    break;
                default:
                    role.setRoleKey("member");
                    role.setIsDefault(true);
                    role.setRoleDes("企业的普通成员");
                    break;
            }
            role.setOrganizationId(orgId);
            role.setCreateTime(new Timestamp(System.currentTimeMillis()));
            role.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            role.setIsSystemInit(true);
            roleMapper.insert(role);
        }
        resourcesRoleService.saveBatch(orgId);
        return 1;
    }

    @Override
    public Integer getOrgRoleIdByKey(String orgId, String roleKey) {

        //构造出查询该企业超级管理员id的条件表达式
        LambdaQueryWrapper<Role> selectAdministratorId = new QueryWrapper<Role>().lambda()
                .eq(Role::getOrganizationId, orgId)
                .eq(Role::getRoleKey, roleKey);
        Role role = roleMapper.selectOne(selectAdministratorId);
        if (role != null) {
            return role.getRoleId();
        }
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer updateOrgDefaultRole(String orgId, String roleId) {
        //取消掉该企业下所有的默认企业
        LambdaUpdateWrapper<Role> cancelAllDefaultRoleUw = new UpdateWrapper<Role>().lambda().eq(Role::getOrganizationId, orgId);
        Role canDefaultRole = new Role();
        canDefaultRole.setIsDefault(false);
        roleMapper.update(canDefaultRole, cancelAllDefaultRoleUw);

        //设置roleId的角色为orgId企业的默认角色
        Role setDefaultRole = new Role();
        setDefaultRole.setIsDefault(true);
        //追加设置该企业下默认角色的条件
        cancelAllDefaultRoleUw.eq(Role::getRoleId, roleId);
        roleMapper.update(setDefaultRole, cancelAllDefaultRoleUw);
        return 1;
    }

    @Override
    public Role getOrgDefaultRole(String orgId) {

        LambdaQueryWrapper<Role> selectOrgDefaultRoleQw = new QueryWrapper<Role>().lambda().eq(Role::getOrganizationId, orgId).eq(Role::getIsDefault, true);
        return roleMapper.selectOne(selectOrgDefaultRoleQw);
    }

    @Override
    public List<Role> getUserOrgRoles(String userId, String orgId) {
        List<Integer> userRoleIds = this.getUserOrgRoleIds(userId, orgId);
        if (CollectionUtils.isEmpty(userRoleIds)) {
            return null;
        }

        List<Role> userRoles = new ArrayList<>();
        //依次查询出该角色信息以及该角色对应的权限信息
        userRoleIds.forEach(roleId -> {
            userRoles.add(this.getRoleAndResourcesInfo(roleId));
        });
        return userRoles;
    }

    @Override
    public List<Integer> getUserOrgRoleIds(String userId, String orgId) {
        List<Integer> roles = roleUserService.getUserOrgRoleIds(userId, orgId);
        if (CollectionUtils.isEmpty(roles)) {
            return new ArrayList<>();
        } else {
            return roles;
        }
    }

    /**
     * 根据角色id获取到该角色信息以及角色下的权限信息
     *
     * @param roleId 角色id
     * @return 角色和角色下的权限信息
     * @author heShaoHua
     * @describe 暂无
     * @updateInfo 暂无
     * @date 2019/6/4 15:54
     */
    @Override
    public Role getRoleAndResourcesInfo(Integer roleId) {
        return roleMapper.selectRoleAndResrouceInfo(roleId);
    }

    @Override
    public List<Role> roleForMember(String userId, String orgId) {
        List<Role> roles = list(new QueryWrapper<Role>().eq("organization_id", orgId));

        Iterator<Role> iterator = roles.iterator();
        while (iterator.hasNext()) {
            Role role = iterator.next();
            RoleUser roleUser = roleUserService.getOne(new QueryWrapper<RoleUser>().eq("u_id", userId).eq("org_id", orgId));
            if (roleUser != null) {
                if (roleUser.getRoleId().equals(role.getRoleId())) {
                    OrganizationMember organizationMember = new OrganizationMember();
                    organizationMember.setMemberLabel(role.getRoleName());
                    organizationMember.setUpdateTime(System.currentTimeMillis());
                    organizationMemberService.update(organizationMember, new QueryWrapper<OrganizationMember>().eq("organization_id", orgId).eq("member_id", userId));
                    role.setCurrentCheck(true);
                } else {
                    role.setCurrentCheck(false);
                }
            }
            if (Constants.OWNER_CN.equals(role.getRoleName())) {
                iterator.remove();
            }
        }

        /*roles.forEach(role -> {
            RoleUser roleUser = roleUserService.getOne(new QueryWrapper<RoleUser>().eq("u_id", userId).eq("org_id", orgId));
            if (roleUser != null) {
                if (roleUser.getRoleId().equals(role.getRoleId())) {
                    OrganizationMember organizationMember = new OrganizationMember();
                    organizationMember.setMemberLabel(role.getRoleName());
                    organizationMember.setUpdateTime(System.currentTimeMillis());
                    organizationMemberService.update(organizationMember, new QueryWrapper<OrganizationMember>().eq("organization_id", orgId).eq("member_id", userId));
                    role.setCurrentCheck(true);
                } else {
                    role.setCurrentCheck(false);
                }
            }
        });*/
        return roles;
    }

    @Override
    public Integer getOrgRoleIdByName(String orgId, String roleName) {
        //构造出查询该企业超级管理员id的条件表达式
        LambdaQueryWrapper<Role> selectAdministratorId = new QueryWrapper<Role>().lambda()
                .eq(Role::getOrganizationId, orgId)
                .eq(Role::getRoleName, roleName);
        Role role = roleMapper.selectOne(selectAdministratorId);
        if (role != null) {
            return role.getRoleId();
        }
        return 0;
    }

    @Override
    public Role checkRole(String orgId, String memberId) {
        return roleMapper.checkRole(orgId, memberId);
    }


}
