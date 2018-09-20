package com.art1001.supply.service.project;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.organization.OrganizationMember;


/**
 * projectService接口
 */
public interface OrganizationMemberService {

	/**
	 * 查询分页project数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<OrganizationMember> findOrganizationMemberPagerList(Pager pager);

	/**
	 * 通过id获取单条project数据
	 * 
	 * @param id
	 * @return
	 */
	public OrganizationMember findOrganizationMemberById(String id);

	/**
	 * 通过id删除project数据
	 * 
	 * @param id
	 */
	public void deleteOrganizationMemberById(String id);

	/**
	 * 修改project数据
	 * 
	 * @param organizationMember
	 */
	public void updateOrganizationMember(OrganizationMember organizationMember);

	/**
	 * 保存project数据
	 * 
	 * @param organizationMember
	 */
	public void saveOrganizationMember(OrganizationMember organizationMember);

	/**
	 * 获取所有project数据
	 * 
	 * @return
	 */
	public List<OrganizationMember> findOrganizationMemberAllList(OrganizationMember organizationMember);

	/**
	 * 通过用户id查询企业用户
	 * @param memberId
	 * @return
	 */
	OrganizationMember findOrgByMemberId(String memberId);
	
}