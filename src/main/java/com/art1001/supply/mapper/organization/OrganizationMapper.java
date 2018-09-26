package com.art1001.supply.mapper.organization;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.organization.Organization;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * organizationmapper接口
 */
@Mapper
public interface OrganizationMapper extends BaseMapper<Organization> {

	/**
	 * 查询分页organization数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<Organization> findOrganizationPagerList(Pager pager);

	/**
	 * 通过organizationId获取单条organization数据
	 * 
	 * @param organizationId
	 * @return
	 */
	Organization findOrganizationByOrganizationId(String organizationId);

	/**
	 * 通过organizationId删除organization数据
	 * 
	 * @param organizationId
	 */
	void deleteOrganizationByOrganizationId(String organizationId);

	/**
	 * 修改organization数据
	 * 
	 * @param organization
	 */
	void updateOrganization(Organization organization);

	/**
	 * 保存organization数据
	 * 
	 * @param organization
	 */
	void saveOrganization(Organization organization);

	/**
	 * 获取所有organization数据
	 * 
	 * @return
	 */
	List<Organization> findOrganizationAllList();

	/**
	 * 查询出 参与的所有企业 以及 项目
	 * @param userId
	 * @return
	 */
	List<Organization> selectJoinOrgProject(String userId);

}