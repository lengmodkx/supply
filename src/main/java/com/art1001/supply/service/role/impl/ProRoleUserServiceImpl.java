package com.art1001.supply.service.role.impl;

import com.art1001.supply.entity.role.ProRole;
import com.art1001.supply.entity.role.ProRoleUser;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.role.ProRoleUserMapper;
import com.art1001.supply.service.role.ProRoleService;
import com.art1001.supply.service.role.ProRoleUserService;
import com.art1001.supply.util.ValidatedUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * <p>
 * 用户角色映射表 服务实现类
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
@Service
public class ProRoleUserServiceImpl extends ServiceImpl<ProRoleUserMapper, ProRoleUser> implements ProRoleUserService {

    @Resource
    private ProRoleService proRoleService;

    @Resource
    private ProRoleUserService roleUserService;
    @Override
    public void distributionRoleToUser(Integer roleId, String userId, String projectId) {
        ProRoleUser proRoleUser = new ProRoleUser();
        proRoleUser.setRoleId(roleId);
        updateById(proRoleUser);
        update(proRoleUser,new UpdateWrapper<ProRoleUser>().eq("project_id", projectId).eq("u_id", userId));
    }

    @Override
    public Boolean checkRelationIsExist(Integer roleId, String userId) {
        ValidatedUtil.filterNullParam(roleId, userId);

        LambdaQueryWrapper<ProRoleUser> eq = new QueryWrapper<ProRoleUser>().lambda()
                .eq(ProRoleUser::getRoleId, roleId)
                .eq(ProRoleUser::getUId, userId);

        return this.count(eq) > 0;
    }

    @Override
    public ProRole getRoleIdForProjectUser(String projectId, String userId) {
        ValidatedUtil.filterNullParam(projectId, userId);

        LambdaQueryWrapper<ProRoleUser> eq = new QueryWrapper<ProRoleUser>().lambda()
                .eq(ProRoleUser::getProjectId, projectId)
                .eq(ProRoleUser::getUId, userId);

        ProRoleUser one = roleUserService.getOne(eq);
        if(one != null){
            return proRoleService.getById(one.getRoleId());
        }
        return null;
    }

    @Override
    public ProRole getRoleOnOrgForUser(String projectId, String userId) {
        ValidatedUtil.filterNullParam(projectId, userId);

        LambdaQueryWrapper<ProRoleUser> getRoleOnOrgRoleInfo = new QueryWrapper<ProRoleUser>().lambda();
        getRoleOnOrgRoleInfo.eq(ProRoleUser::getProjectId, projectId).eq(ProRoleUser::getUId, userId);

        ProRoleUser proRoleUser = this.getOne(getRoleOnOrgRoleInfo);

        Optional.ofNullable(proRoleUser).orElseThrow(() -> new ServiceException("用户在企业中没有角色!"));

        return proRoleService.getById(proRoleUser.getRoleId());
    }
}
