package com.art1001.supply.service.project;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * projectService接口
 */
public interface OrganizationMemberService extends IService<OrganizationMember> {

	/**
	 * 查询企业员工
	 * @param orgId 企业id
	 * @return
	 */
	List<UserEntity> getUserList(String orgId);
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
	OrganizationMember findOrgByMemberId(String memberId, String orgId);

	/**
	 * 获取用户已经加入的企业数量
	 * @return 加入的企业数量
	 */
	int userOrgCount();

	/**
	 * 修改一个用户的默认企业
	 * @author heShaoHua
	 * @describe 暂无
	 * @param userId 用户id
	 * @param orgId 企业id
	 * @updateInfo 暂无
	 * @date 2019/5/29 11:08
	 * @return 结果
	 */
	Integer updateUserDefaultOrg(String orgId, String userId);

	/**
	 * 保存企业的创建人信息
	 * @author heShaoHua
	 * @describe 暂无
	 * @param orgId 企业id
	 * @param userId 用户id
	 * @updateInfo 暂无
	 * @date 2019/5/29 15:14
	 * @return 结果
	 */
	Integer saveOrgOwnerInfo(String orgId, String userId);

	/**
	 * 查询默认企业id
	 * @param memberId
	 * @return
	 */
	String findOrgByUserId(String memberId);



	/**
	 * 移交企业权限
	 *
	 * @param orgId 企业id
	 * @param ownerId 企业拥有者id
	 * @param memberId 员工id
	 * @return
	 */
	Boolean transferOwner(String orgId, String ownerId, String memberId);

	/**
	 * 根据关键字获取企业中的用户信息列表
	 * @param orgId 企业id
	 * @param keyword 关键字
	 * @return 用户信息集合
	 */
    List<UserEntity> getOrgMemberByKeyword(String orgId, String keyword);

	/**
	 * 根据企业id删除企业成员记录
	 * @param orgId 企业id
	 */
	void removeMemberByOrgId(String orgId);

	/**
	 * 查询userId的用户是否是orgId企业的拥有者
	 * @param orgId 企业id
	 * @param userId 用户id
	 * @return 是否是拥有者
	 */
    Boolean checkUserIdIsOrgMaster(String orgId, String userId);



}