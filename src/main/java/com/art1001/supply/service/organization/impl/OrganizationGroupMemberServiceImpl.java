package com.art1001.supply.service.organization.impl;

import com.art1001.supply.entity.organization.OrganizationGroup;
import com.art1001.supply.entity.organization.OrganizationGroupMember;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.organization.OrganizationGroupMapper;
import com.art1001.supply.mapper.organization.OrganizationGroupMemberMapper;
import com.art1001.supply.service.organization.OrganizationGroupMemberService;
import com.art1001.supply.util.Stringer;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DindDangMao
 * @since 2019-04-29
 */
@Service
public class OrganizationGroupMemberServiceImpl extends ServiceImpl<OrganizationGroupMemberMapper, OrganizationGroupMember> implements OrganizationGroupMemberService {

    @Resource
    private OrganizationGroupMemberMapper organizationGroupMemberMapper;

    @Resource
    private OrganizationGroupMapper organizationGroupMapper;

    /**
     * 群组中添加组成员
     * @param groupId 群组id
     * @param memberId 成员id
     * @return 结果
     */
    @Override
    public Boolean addGroupMember(String groupId, String memberId) {
        if(this.checkMemberIsExist(groupId, memberId)){
            throw new ServiceException("成员已经存在,不能重复添加!");
        }
        OrganizationGroupMember organizationGroupMember = new OrganizationGroupMember();
        organizationGroupMember.setGroupId(groupId);
        organizationGroupMember.setMemberId(memberId);
        organizationGroupMember.setUpdateTime(System.currentTimeMillis());
        organizationGroupMember.setCreateTime(System.currentTimeMillis());
        return organizationGroupMemberMapper.insert(organizationGroupMember) > 0;
    }

    /**
     * 该成员在分组中是否存在
     * @param groupId 群组id
     * @param memberId 成员id
     * @return 结果
     */
    @Override
    public Boolean checkMemberIsExist(String groupId, String memberId) {
        return organizationGroupMemberMapper.selectCount(new QueryWrapper<OrganizationGroupMember>().eq("group_id", groupId).eq("member_id", memberId)) > 0;
    }

    /**
     * 获取一个群组下某个成员的信息
     * @param groupId 群组id
     * @param memberId 成员id
     * @return 成员信息
     */
    @Override
    public OrganizationGroupMember getGroupMemberBymemberId(String groupId, String memberId) {
        return organizationGroupMemberMapper.selectGroupMemberBymemberId(groupId,memberId);
    }

    /**
     * 移除一个成员
     * 如果这个成员是拥有者那么这个群组的拥有者将移交给第二个人 (按照加入时间升序排列结果的第一个人 也就是最早加入的人)
     * 如果当前退出的成员是该群组的最后一个人 那么成员退出后  群组将被删除
     * @param memberId 成员id
     * @param groupId 群组id
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean removeMember(String memberId, String groupId) {
        if(Stringer.isNullOrEmpty(memberId)){
            throw new ServiceException("成员id不能为空!");
        }
        organizationGroupMemberMapper.delete(new QueryWrapper<OrganizationGroupMember>().eq("group_id", groupId).eq("member_id", memberId));
        //如果当前用户退出后,群组内人数 < = 0 就需要删除分组
        if(organizationGroupMemberMapper.selectCount(new QueryWrapper<OrganizationGroupMember>().eq("group_id", groupId)) <= 0){
            organizationGroupMapper.deleteById(groupId);
        } else {
            if(memberId.equals(organizationGroupMapper.selectById(groupId).getOwner())){
                OrganizationGroup organizationGroup = new OrganizationGroup();
                organizationGroup.setGroupId(groupId);
                organizationGroup.setOwner(organizationGroupMemberMapper.selectEarliestMemberId(groupId));
                organizationGroup.setUpdateTime(System.currentTimeMillis());
                organizationGroupMapper.updateById(organizationGroup);
            }
        }
        return true;
    }
}
