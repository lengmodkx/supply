package com.art1001.supply.service.role.impl;

import com.art1001.supply.entity.role.RoleUser;
import com.art1001.supply.mapper.role.RoleUserMapper;
import com.art1001.supply.service.role.RoleUserService;
import com.art1001.supply.util.ValidatedUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

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
}
