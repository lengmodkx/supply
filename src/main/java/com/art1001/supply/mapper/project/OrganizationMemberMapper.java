package com.art1001.supply.mapper.project;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.project.OrganizationMember;
import org.apache.ibatis.annotations.Mapper;

/**
 * projectmapper接口
 */
@Mapper
public interface OrganizationMemberMapper {

	/**
	 * 查询分页project数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<OrganizationMember> findOrganizationMemberPagerList(Pager pager);

	/**
	 * 通过id获取单条project数据
	 * 
	 * @param id
	 * @return
	 */
	OrganizationMember findOrganizationMemberById(String id);

	/**
	 * 通过id删除project数据
	 * 
	 * @param id
	 */
	void deleteOrganizationMemberById(String id);

	/**
	 * 修改project数据
	 * 
	 * @param organizationMember
	 */
	void updateOrganizationMember(OrganizationMember organizationMember);

	/**
	 * 保存project数据
	 * 
	 * @param organizationMember
	 */
	void saveOrganizationMember(OrganizationMember organizationMember);

	/**
	 * 获取所有project数据
	 * 
	 * @return
	 */
	List<OrganizationMember> findOrganizationMemberAllList();

	/**
	 * 通过用户id查询企业用户
	 * @param memberId
	 * @return
	 */
	OrganizationMember findOrgByMemberId(String memberId);

}