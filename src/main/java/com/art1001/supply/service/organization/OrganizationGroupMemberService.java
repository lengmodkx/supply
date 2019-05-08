package com.art1001.supply.service.organization;

import com.art1001.supply.entity.organization.OrganizationGroupMember;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author shaohua
 * @since 2019-04-29
 */
public interface OrganizationGroupMemberService extends IService<OrganizationGroupMember> {

    /**
     * 群组中添加组成员
     * @param groupId 群组id
     * @param memberId 成员id
     * @return 结果
     */
    Boolean addGroupMember(String groupId, String memberId);

    /**
     * 该成员在分组中是否存在
     * @param groupId 群组id
     * @param memberId 成员id
     * @return 结果
     */
    Boolean checkMemberIsExist(String groupId, String memberId);

    /**
     * 获取一个群组下某个成员的信息
     * @param groupId 群组id
     * @param memberId 成员id
     * @return 成员信息
     */
    OrganizationGroupMember getGroupMemberBymemberId(String groupId, String memberId);

    /**
     * 移除一个成员
     * 如果这个成员是拥有者那么这个群组的拥有者将移交给第二个人 (按照加入时间升序排列结果的第一个人 也就是最早加入的人)
     * 如果当前退出的成员是该群组的最后一个人 那么成员退出后  群组将被删除
     * @param memberId 成员id
     * @param groupId 群组id
     * @return 结果
     */
    Boolean removeMember(String memberId, String groupId);

    /**
     * 获取群组下的所有成员信息
     * @param groupId 群组id
     * @return 群组内所有成员信息
     */
    List<OrganizationGroupMember> getGroupMembers(String groupId);


}
