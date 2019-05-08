package com.art1001.supply.mapper.organization;

import java.util.List;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.organization.OrganizationGroup;
import com.art1001.supply.entity.organization.OrganizationGroupMember;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 组织群组mapper接口
 */
@Mapper
public interface OrganizationGroupMapper extends BaseMapper<OrganizationGroup> {

	/**
	 * 查询分页组织群组数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<OrganizationGroup> findOrganizationGroupPagerList(Pager pager);

	/**
	 * 通过groupId获取单条组织群组数据
	 * 
	 * @param groupId
	 * @return
	 */
	OrganizationGroup findOrganizationGroupByGroupId(String groupId);

	/**
	 * 通过groupId删除组织群组数据
	 * 
	 * @param groupId
	 */
	void deleteOrganizationGroupByGroupId(String groupId);

	/**
	 * 修改组织群组数据
	 * 
	 * @param organizationGroup
	 */
	void updateOrganizationGroup(OrganizationGroup organizationGroup);

	/**
	 * 保存组织群组数据
	 * 
	 * @param organizationGroup
	 */
	void saveOrganizationGroup(OrganizationGroup organizationGroup);

	/**
	 * 获取所有组织群组数据
	 * 
	 * @return
	 */
	List<OrganizationGroup> findOrganizationGroupAllList();

	/**
	 * 根据企业id 获取企业下的所有群组信息
	 * @param orgId orgId 企业id
	 * @param userId 当前用户id
	 * @return 企业群组信息
	 */
    List<OrganizationGroup> selectOrgGroups(@Param("orgId") String orgId, @Param("userId") String userId);

	/**
	 * 获取某个群组的拥有者信息
	 * @param groupId 群组id
	 * @return 拥有者信息
	 */
	OrganizationGroupMember selectOwnerInfo(@Param("groupId") String groupId);
}