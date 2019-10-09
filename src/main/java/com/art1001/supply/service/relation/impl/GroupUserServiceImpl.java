package com.art1001.supply.service.relation.impl;

import com.art1001.supply.application.assembler.GroupUserAssembler;
import com.art1001.supply.entity.relation.GroupUser;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.relation.GroupUserMapper;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.relation.GroupUserService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.util.ValidatedUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author heshaohua
 * @since 2019-10-09
 */
@Service
public class GroupUserServiceImpl extends ServiceImpl<GroupUserMapper, GroupUser> implements GroupUserService {

    @Resource
    private GroupUserAssembler groupUserAssembler;

    @Resource
    private ProjectMemberService projectMemberService;

    @Resource
    private RelationService relationService;

    @Override
    public void additionGroupUser(String groupId, String userId) {
        if(this.checkGroupUserIsExist(groupId, userId) > 0){
            throw new ServiceException("分组中已经存在该成员，不能重复添加！");
        }

        if(!this.checkGroupAndUserProjectIsIdentical(groupId, userId)){
            throw new ServiceException("要添加的成员所属项目和该分组的所属项目不一致，无法完成添加操作！");
        }

        GroupUser groupUser = groupUserAssembler.transFormGroupUser(groupId, userId);

        this.save(groupUser);
    }

    @Override
    public Integer checkGroupUserIsExist(String groupId, String userId) {
        ValidatedUtil.filterNullParam(groupId, userId);

        LambdaQueryWrapper<GroupUser> selectGroupUserIsExistQw = new QueryWrapper<GroupUser>().lambda()
                .eq(GroupUser::getGroupId, groupId)
                .eq(GroupUser::getUserId, userId);

        return this.count(selectGroupUserIsExistQw);
    }

    @Override
    public Boolean checkGroupAndUserProjectIsIdentical(String groupId, String userId) {
        ValidatedUtil.filterNullParam(groupId, userId);

        String groupProjectId = relationService.getProjectId(groupId);

        List<String> projectIdList = projectMemberService.getUserProjectIdList(userId);

        return projectIdList.contains(groupProjectId);
    }
}
