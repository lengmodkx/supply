package com.art1001.supply.service.organization;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.organization.OrganizationGroup;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 组织群组Service接口
 */
public interface OrganizationGroupService extends IService<OrganizationGroup> {

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

	/**
	 * 创建企业群组
	 * @param organizationGroup 群组信息对象
	 * @param memberIds 成员id 数组
	 * @return 结果
	 */
	Boolean createGroup(OrganizationGroup organizationGroup, String[] memberIds);

	/**
	 * 删除群组
	 * @param groupId 群组id
	 * @return 结果
	 */
    Boolean removeGroup(String groupId);
}