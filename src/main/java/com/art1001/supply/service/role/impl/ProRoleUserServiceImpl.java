package com.art1001.supply.service.role.impl;

import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.role.ProRole;
import com.art1001.supply.entity.role.ProRoleUser;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.role.ProRoleUserMapper;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.role.ProRoleService;
import com.art1001.supply.service.role.ProRoleUserService;
import com.art1001.supply.util.ValidatedUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
 * @since 2019-06-18
 */
@Service
public class ProRoleUserServiceImpl extends ServiceImpl<ProRoleUserMapper, ProRoleUser> implements ProRoleUserService {

    @Resource
    private ProRoleService proRoleService;

    @Resource
    private ProRoleUserService roleUserService;

    @Resource
    private ProjectMemberService projectMemberService;

    @Resource
    private ProRoleUserMapper proRoleUserMapper;

    @Override
    public void distributionRoleToUser(Integer roleId, String userId, String projectId) {
        ProRoleUser proRoleUser = new ProRoleUser();
        proRoleUser.setRoleId(roleId);
        update(proRoleUser,new UpdateWrapper<ProRoleUser>().eq("project_id", projectId).eq("u_id", userId));

        ProRole proRole = proRoleService.getById(roleId);
        ProjectMember projectMember = new ProjectMember();
        projectMember.setRoleKey(proRole.getRoleKey());
        projectMemberService.update(projectMember,new UpdateWrapper<ProjectMember>().eq("project_id", projectId).eq("member_id", userId));
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

    @Override
    public List<String> getRoleUserIdListByRoleId(Integer roleId) {
        ValidatedUtil.filterNullParam(roleId);

        LambdaQueryWrapper<ProRoleUser> eq = new QueryWrapper<ProRoleUser>().lambda()
                .eq(ProRoleUser::getRoleId, roleId);

        List<ProRoleUser> proRoleUserList = this.list(eq);
        if(CollectionUtils.isEmpty(proRoleUserList)){
            return new LinkedList<>();
        }

        return proRoleUserList.stream().map(ProRoleUser::getUId).collect(Collectors.toList());
    }

    /**
     * 获取项目所有角色
     * @param projectId
     * @return
     */
    @Override
    public Integer getManagersByProject(String projectId) {
        return proRoleUserMapper.getManagersByProject(projectId);
    }

    @Override
    public ProRoleUser findProRoleUser(String projectId, String memberId) {
        return proRoleUserMapper.findProRoleUser(projectId,memberId);
    }
}
