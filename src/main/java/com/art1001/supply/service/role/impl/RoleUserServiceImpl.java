package com.art1001.supply.service.role.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.role.ProRoleUser;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.entity.role.RoleUser;
import com.art1001.supply.mapper.role.RoleUserMapper;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.service.role.RoleUserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.ValidatedUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户角色映射表 服务实现类
 * </p>
 *
 * @author heshaohua
 * @since 2019-05-28
 */
@Service
public class RoleUserServiceImpl extends ServiceImpl<RoleUserMapper, RoleUser> implements RoleUserService {

    @Resource
    private RoleUserMapper roleUserMapper;

    @Resource
    private RoleService roleService;

    @Override
    public List<Integer> getUserOrgRoleIds(String userId, String orgId) {
        return roleUserMapper.selectUserOrgRoleIds(userId,orgId);
    }

    @Override
    public Integer
    getUserOrgRoleId(String userId, String orgId) {
        ValidatedUtil.filterNullParam(userId, orgId);

        LambdaQueryWrapper<RoleUser> getRoleIdByUserIdAndOrgId = new QueryWrapper<RoleUser>().lambda()
                .select(RoleUser::getRoleId).eq(RoleUser::getUId, userId).eq(RoleUser::getOrgId, orgId);

        return Optional.ofNullable(this.getOne(getRoleIdByUserIdAndOrgId))
                .map(RoleUser::getRoleId).orElse(null);
    }


    @Override
    public Boolean updateRoleTransfer(String orgId, String ownerId, String memberId) {

        Integer RoleIdByOwner = this.getOne(new QueryWrapper<RoleUser>().lambda()
                .select(RoleUser::getRoleId).eq(RoleUser::getUId, ownerId).eq(RoleUser::getOrgId, orgId)).getRoleId();
        Integer RoleIdByMember = this.getOne( new QueryWrapper<RoleUser>().lambda()
                .select(RoleUser::getRoleId).eq(RoleUser::getUId, memberId).eq(RoleUser::getOrgId, orgId)).getRoleId();
        Boolean updateRoleMember= roleUserMapper.updateRoleOwner(orgId,ownerId,RoleIdByMember);
        Boolean updateRoleOwner= roleUserMapper.updateRoleMember(orgId,memberId,RoleIdByOwner);
       if (updateRoleMember && updateRoleOwner){
           return true;
       }
        return  false;
    }

    @Override
    public List<String> getRoleUserIdListByRoleId(Integer roleId) {
        ValidatedUtil.filterNullParam(roleId);
        LambdaQueryWrapper<RoleUser> eq = new QueryWrapper<RoleUser>().lambda()
                .eq(RoleUser::getRoleId, roleId);

        //获取list
        List<RoleUser> proRoleUserList = this.list(eq);
        if(CollectionUtils.isEmpty(proRoleUserList)){
            return new LinkedList<>();
        }

        return proRoleUserList.stream().map(RoleUser::getUId).collect(Collectors.toList());
    }

    @Override
    public Integer isOwner(String orgId) {
        RoleUser roleUser = roleUserMapper.selectOne(new QueryWrapper<RoleUser>().eq("u_id", ShiroAuthenticationManager.getUserId()).eq("org_id", orgId));
        Role role = roleService.getOne(new QueryWrapper<Role>().eq("role_id", roleUser.getRoleId()));
        if (!Constants.OWNER_CN.equals(role.getRoleName())&&!Constants.ADMIN_CN.equals(role.getRoleName())) {
            return 0;
        }
        return 1;
    }

    @Override
    public RoleUser getOrgOwenr(String orgId) {
        return roleUserMapper.getOrgOwenr(orgId);
    }
}
