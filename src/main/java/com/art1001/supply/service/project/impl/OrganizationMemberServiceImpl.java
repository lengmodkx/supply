package com.art1001.supply.service.project.impl;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.entity.role.RoleUser;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.organization.OrganizationMapper;
import com.art1001.supply.mapper.project.OrganizationMemberMapper;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.service.role.RoleUserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * projectServiceImpl
 */
@Service
public class OrganizationMemberServiceImpl extends ServiceImpl<OrganizationMemberMapper,OrganizationMember> implements OrganizationMemberService {

	/** projectMapper接口*/
	@Resource
	private OrganizationMemberMapper organizationMemberMapper;

	/** organizationMapper接口*/
	@Resource
	private OrganizationMapper organizationMapper;


	@Resource
	private RoleUserService roleUserService;

	@Resource
	private RoleService roleService;



	/**
	 * 根据企业id获取企业员工
	 * @param orgId 企业id
	 * @return 员工信息
	 */
	@Override
	public List<UserEntity> getUserList(String orgId) {
		return organizationMemberMapper.getUserList(orgId);
	}

	/**
	 * 查询分页project数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<OrganizationMember> findOrganizationMemberPagerList(Pager pager){
		return organizationMemberMapper.findOrganizationMemberPagerList(pager);
	}

	/**
	 * 通过id获取单条project数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public OrganizationMember findOrganizationMemberById(String id){
		return organizationMemberMapper.findOrganizationMemberById(id);
	}

	/**
	 * 通过id删除project数据
	 * 
	 * @param id
	 */
	@Override
	public void deleteOrganizationMemberById(String id){
		organizationMemberMapper.deleteOrganizationMemberById(id);
	}

	/**
	 * 修改project数据
	 * 
	 * @param organizationMember
	 */
	@Override
	public void updateOrganizationMember(OrganizationMember organizationMember){
		organizationMemberMapper.updateOrganizationMember(organizationMember);
	}
	/**
	 * 保存project数据
	 * 
	 * @param organizationMember
	 */
	@Override
	public void saveOrganizationMember(OrganizationMember organizationMember){
		OrganizationMember organizationMember1 = getOne(new QueryWrapper<OrganizationMember>().eq("member_id", organizationMember.getMemberId()).eq("organization_id", organizationMember.getOrganizationId()));
		if(organizationMember1==null){
			organizationMember.setCreateTime(System.currentTimeMillis());
			organizationMember.setUpdateTime(System.currentTimeMillis());
			organizationMemberMapper.insert(organizationMember);
			//修改企业成员默认权限，2020-20-10 汪亚锋
			Role role;
			if(organizationMember.getOther()==0){
				role = roleService.getOne(new QueryWrapper<Role>().eq("role_key","externalMember").eq("organization_id", organizationMember.getOrganizationId()));
			}else{
				role = roleService.getOrgDefaultRole(organizationMember.getOrganizationId());
			}

			RoleUser roleUser = new RoleUser();
			roleUser.setOrgId(organizationMember.getOrganizationId());
			roleUser.setRoleId(role.getRoleId());
			roleUser.setUId(organizationMember.getMemberId());
			roleUser.setTCreateTime(LocalDateTime.now());
			roleUserService.save(roleUser);
		}
	}
	/**
	 * 获取所有project数据
	 * 
	 * @return
	 */
	@Override
	public List<OrganizationMember> findOrganizationMemberAllList(OrganizationMember organizationMember){
		return organizationMemberMapper.findOrganizationMemberAllList(organizationMember);
	}

	@Override
	public OrganizationMember findOrgByMemberId(String memberId, String orgId) {
		return organizationMemberMapper.findOrgByMemberId(memberId,orgId);
	}

	/**
	 * 获取用户已经加入的企业数量
	 * @return 加入的企业数量
	 */
	@Override
	public int userOrgCount() {
		return organizationMemberMapper.selectCount(new QueryWrapper<OrganizationMember>().eq("member_id",ShiroAuthenticationManager.getUserId()));
	}

	/**
	 * 修改一个用户的默认企业
	 * @param orgId  企业id
	 * @param userId 用户id
	 * @return 结果
	 * @author heShaoHua
	 * @describe 失败返回-1
	 * @updateInfo 暂无
	 * @date 2019/5/29 11:08
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Integer updateUserDefaultOrg(String orgId, String userId) {
		//构造修改数据信息
		OrganizationMember organizationMember = new OrganizationMember();
		organizationMember.setUserDefault(false);
		organizationMember.setUpdateTime(System.currentTimeMillis());

		//构造出条件对象(清除当前用户的企业记录)
		LambdaQueryWrapper<OrganizationMember> clear = new QueryWrapper<OrganizationMember>().lambda()
				.eq(OrganizationMember::getMemberId, userId)
				.eq(OrganizationMember::getUserDefault, true);

		//构造出条件对象(标记出新的用户企业记录)
		LambdaQueryWrapper<OrganizationMember> sign = new QueryWrapper<OrganizationMember>().lambda()
				.eq(OrganizationMember::getMemberId, userId)
				.eq(OrganizationMember::getOrganizationId, orgId);

		organizationMemberMapper.update(organizationMember,clear);
		organizationMember.setUserDefault(true);
		organizationMemberMapper.update(organizationMember,sign);
		return 1;
	}

	/**
	 * 保存企业的创建人信息
	 * @param orgId  企业id
	 * @param userId 用户id
	 * @return 结果
	 * @author heShaoHua
	 * @describe 暂无
	 * @updateInfo 暂无
	 * @date 2019/5/29 15:14
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Integer saveOrgOwnerInfo(String orgId, String userId) {
		//添加当前用户为企业拥有者
		OrganizationMember organizationMember = new OrganizationMember();
		organizationMember.setOrganizationId(orgId);
		organizationMember.setMemberId(userId);
		organizationMember.setCreateTime(System.currentTimeMillis());
		organizationMember.setUpdateTime(System.currentTimeMillis());
		organizationMember.setOrganizationLable(1);
		organizationMemberMapper.insert(organizationMember);

		this.updateUserDefaultOrg(orgId, userId);
		return 1;
	}

	@Override
	public String findOrgByUserId(String memberId) {
		return organizationMemberMapper.findOrgByUserId(memberId);
	}


	/**
	 * 移交企业权限
	 *
	 * @param orgId 企业id
	 * @param ownerId 企业拥有者id
	 * @param memberId 员工id
	 * @return
	 */
	@Override
	public Boolean transferOwner(String orgId, String ownerId, String memberId){

		try {
			Boolean updateOwner= organizationMemberMapper.updateOwner(orgId,ownerId);
			Boolean updateMember=organizationMemberMapper.updateMember(orgId,memberId);
			Boolean updatOorganization = organizationMapper.updatOorganization(orgId,ownerId,memberId);
			//将userRole表权限互换
			Boolean updateRoleTransfer=roleUserService.updateRoleTransfer(orgId,ownerId,memberId);
			if (updateOwner && updateMember && updatOorganization && updateRoleTransfer){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}