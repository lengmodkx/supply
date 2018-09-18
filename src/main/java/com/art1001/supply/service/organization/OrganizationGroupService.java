package com.art1001.supply.service.organization;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.organization.OrganizationGroup;

/**
 * 组织群组Service接口
 */
public interface OrganizationGroupService {

	/**
	 * 查询分页组织群组数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<OrganizationGroup> findOrganizationGroupPagerList(Pager pager);

	/**
	 * 通过groupId获取单条组织群组数据
	 * 
	 * @param groupId
	 * @return
	 */
	public OrganizationGroup findOrganizationGroupByGroupId(String groupId);

	/**
	 * 通过groupId删除组织群组数据
	 * 
	 * @param groupId
	 */
	public void deleteOrganizationGroupByGroupId(String groupId);

	/**
	 * 修改组织群组数据
	 * 
	 * @param organizationGroup
	 */
	public void updateOrganizationGroup(OrganizationGroup organizationGroup);

	/**
	 * 保存组织群组数据
	 * 
	 * @param organizationGroup
	 */
	public void saveOrganizationGroup(OrganizationGroup organizationGroup);

	/**
	 * 获取所有组织群组数据
	 * 
	 * @return
	 */
	public List<OrganizationGroup> findOrganizationGroupAllList();
	
}