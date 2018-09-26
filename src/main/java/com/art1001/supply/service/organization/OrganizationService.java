package com.art1001.supply.service.organization;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.organization.Organization;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * organizationService接口
 */
public interface OrganizationService extends IService<Organization> {

	/**
	 * 查询分页organization数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<Organization> findOrganizationPagerList(Pager pager);

	/**
	 * 通过organizationId获取单条organization数据
	 * 
	 * @param organizationId
	 * @return
	 */
	public Organization findOrganizationByOrganizationId(String organizationId);

	/**
	 * 通过organizationId删除organization数据
	 * 
	 * @param organizationId
	 */
	public void deleteOrganizationByOrganizationId(String organizationId);

	/**
	 * 修改organization数据
	 * 
	 * @param organization
	 */
	public void updateOrganization(Organization organization);

	/**
	 * 保存organization数据
	 * 
	 * @param organization
	 */
	public void saveOrganization(Organization organization);

	/**
	 * 获取所有organization数据
	 * 
	 * @return
	 */
	public List<Organization> findOrganizationAllList();

	/**
	 * 获取用户参与的所有企业 以及 企业项目
	 * @param userId
	 * @return
	 */
    List<Organization> findJoinOrgProject(String userId);
}