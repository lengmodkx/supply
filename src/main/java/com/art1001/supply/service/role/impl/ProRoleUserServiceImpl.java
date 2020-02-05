package com.art1001.supply.service.role.impl;

import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.role.ProRole;
import com.art1001.supply.entity.role.ProRoleUser;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.role.ProRoleUserMapper;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.role.ProRoleService;
import com.art1001.supply.service.role.ProRoleUserService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.util.ValidatedUtil;
import com.art1001.supply.util.ValidatorUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
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
    private UserService userService;

    @Resource
    private ProRoleService proRoleService;

    @Resource
    private ProjectMemberService projectMemberService;

    @Override
    public void distributionRoleToUser(Integer roleId, String userId, String projectId) {
        Optional.ofNullable(userService.getById(userId)).orElseThrow(() -> new ServiceException("用户不存在！"));

        if(proRoleService.checkRoleIsNotExistByRoleId(roleId)){
            throw new ServiceException("角色不存在！");
        }

        ProRole roleById = proRoleService.getById(roleId);

        if(!projectId.equals(roleById.getOrgId())){
            throw new ServiceException("角色不属于该项目！");
        }

        if(!projectMemberService.checkUserProjectBindIsExist(projectId, userId)){
            throw new ServiceException("用户不在该项目中。");
        }

//        if(this.checkRelationIsExist(roleId, userId)){
//            throw new ServiceException("对应关系已经存在!");
//        }

        ProjectMember projectMember = new ProjectMember();
        projectMember.setMemberId(userId);
        projectMember.setRoleId(roleId);
        projectMember.setUpdateTime(System.currentTimeMillis());

        projectMemberService.update(projectMember,
                new QueryWrapper<ProjectMember>().lambda()
                .eq(ProjectMember::getMemberId, userId)
                .eq(ProjectMember::getProjectId, projectId)
        );
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

        LambdaQueryWrapper<ProjectMember> eq = new QueryWrapper<ProjectMember>().lambda()
                .eq(ProjectMember::getProjectId, projectId)
                .eq(ProjectMember::getMemberId, userId);

        ProjectMember relation = projectMemberService.getOne(eq);
        if(relation != null){
            return proRoleService.getById(relation.getRoleId());
        }
        return null;
    }
}
