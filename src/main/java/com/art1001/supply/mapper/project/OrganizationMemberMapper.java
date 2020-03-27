package com.art1001.supply.mapper.project;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.user.ProjectMemberInfo;
import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * projectmapper接口
 */
@Mapper
public interface OrganizationMemberMapper extends BaseMapper<OrganizationMember> {

	List<UserEntity> getUserList(String orgId);
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
	List<OrganizationMember> findOrganizationMemberAllList(OrganizationMember organizationMember);

	/**
	 * 通过用户id查询企业用户
	 * @param memberId
	 * @return
	 */
	OrganizationMember findOrgByMemberId(@Param("memberId") String memberId,@Param("orgId") String orgId);

	String findOrgByUserId(String memberId);

	/**
	 * 通过企业id获取员工
	 * @param orgId 企业id
	 * @return
	 */
	List<OrganizationMember> findMemberCompanies(String orgId);

	/**
	 * 移交企业修改拥有者
	 * @param orgId 企业id
	 * @param ownerId 拥有者id
	 * @return
	 */
	Boolean updateOwner(@Param("orgId")String orgId, @Param("ownerId")String ownerId);

	/**
	 * 移交企业修改
	 * @param orgId 企业id
	 * @param memberId 成员id
	 * @return
	 */
	Boolean updateMember(@Param("orgId")String orgId, @Param("memberId")String memberId);

    List<ProjectMemberInfo> getOrgMemberList(@Param("orgId") String orgId);

}