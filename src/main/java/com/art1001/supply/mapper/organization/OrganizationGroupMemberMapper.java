package com.art1001.supply.mapper.organization;

import com.art1001.supply.entity.organization.OrganizationGroupMember;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author DindDangMao
 * @since 2019-04-29
 */
@Mapper
public interface OrganizationGroupMemberMapper extends BaseMapper<OrganizationGroupMember> {

    /**
     * 获取一个群组下某个成员的信息
     * @param groupId 群组id
     * @param memberId 成员id
     * @return 成员信息
     */
    OrganizationGroupMember selectGroupMemberBymemberId(@Param("groupId") String groupId,@Param("memberId") String memberId);

    /**
     * 获取到最早加入到该群组的成员id
     * @param groupId 群组id
     * @return 成员id
     */
    String selectEarliestMemberId(@Param("groupId") String groupId);

    /**
     * 获取群组下的所有成员信息
     * @param groupId 群组id
     * @return 群组内所有成员信息
     */
    List<OrganizationGroupMember> selectGroupMembes(@Param("groupId") String groupId);
}
