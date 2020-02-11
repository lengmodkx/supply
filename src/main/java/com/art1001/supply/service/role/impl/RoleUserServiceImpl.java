package com.art1001.supply.service.role.impl;

import com.art1001.supply.entity.role.RoleUser;
import com.art1001.supply.mapper.role.RoleUserMapper;
import com.art1001.supply.service.role.RoleUserService;
import com.art1001.supply.util.ValidatedUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Map;
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
}
