package com.art1001.supply.service.organization.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.entity.role.RoleUser;
import com.art1001.supply.mapper.organization.OrganizationMapper;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.service.role.RoleUserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import com.art1001.supply.util.Stringer;
import com.art1001.supply.validation.role.RoleIdValidation;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import org.springframework.transaction.annotation.Transactional;

/**
 * organizationServiceImpl
 */
@Service
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper,Organization> implements OrganizationService {

	/** organizationMapper接口*/
	@Resource
	private OrganizationMapper organizationMapper;

	@Resource
	private OrganizationMemberService organizationMemberService;

	@Resource
	private RoleService roleService;

	@Resource
	private RoleUserService roleUserService;

	/**
	 * 查询分页organization数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Organization> findOrganizationPagerList(Pager pager){
		return organizationMapper.findOrganizationPagerList(pager);
	}

	/**
	 * 通过organizationId获取单条organization数据
	 * 
	 * @param organizationId
	 * @return
	 */
	@Override 
	public Organization findOrganizationByOrganizationId(String organizationId){
		return organizationMapper.findOrganizationByOrganizationId(organizationId);
	}

	/**
	 * 通过organizationId删除organization数据
	 * 
	 * @param organizationId
	 */
	@Override
	public void deleteOrganizationByOrganizationId(String organizationId){
		organizationMapper.deleteOrganizationByOrganizationId(organizationId);
	}

	/**
	 * 修改organization数据
	 * 
	 * @param organization
	 */
	@Override
	public void updateOrganization(Organization organization){
		organization.setUpdateTime(System.currentTimeMillis());
		organizationMapper.updateOrganization(organization);
	}
	/**
	 * 保存organization数据
	 * @param organization 企业信息
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void saveOrganization(Organization organization){
		organization.setOrganizationMember(ShiroAuthenticationManager.getUserId());
		organization.setCreateTime(System.currentTimeMillis());
		organization.setUpdateTime(System.currentTimeMillis());
		organizationMapper.insert(organization);

		//添加当前用户为企业拥有者
		OrganizationMember organizationMember = new OrganizationMember();
		organizationMember.setOrganizationId(organization.getOrganizationId());
		organizationMember.setMemberId(ShiroAuthenticationManager.getUserId());
		organizationMember.setCreateTime(System.currentTimeMillis());
		organizationMember.setUpdateTime(System.currentTimeMillis());
		organizationMember.setOrganizationLable(1);
		organizationMember.setUserDefault(true);
		organizationMemberService.save(organizationMember);

		//初始化默认角色
		roleService.saveOrgDefaultRole(organization.getOrganizationId());

		Integer administrator = roleService.getOne(new QueryWrapper<Role>().lambda().eq(Role::getOrganizationId, organization.getOrganizationId()).eq(Role::getRoleKey, "administrator")).getRoleId();
		RoleUser roleUser = new RoleUser();
		roleUser.setTCreateTime(LocalDateTime.now());
		roleUser.setUId(ShiroAuthenticationManager.getUserId());
		roleUser.setRoleId(administrator);
		roleUserService.save(roleUser);
	}
	/**
	 * 获取所有organization数据
	 * 
	 * @return
	 */
	@Override
	public List<Organization> findOrganizationAllList(){
		return organizationMapper.findOrganizationAllList();
	}

	/**
	 * 获取用户参与的所有企业 以及 企业项目
	 * @param userId
	 * @return
	 */
	@Override
	public List<Organization> findJoinOrgProject(String userId) {
		return organizationMapper.selectJoinOrgProject(userId);
	}

	/**
	 * 获取和我相关的企业
	 * @param flag 标识
	 * @return 企业列表
	 */
	@Override
	public List<Organization> getMyOrg(Integer flag) {
		List<Organization> myOrg = organizationMapper.getMyOrg(flag, ShiroAuthenticationManager.getUserId());
		if(CollectionUtils.isEmpty(myOrg)){
			return myOrg;
		}

		//构造出 查询当前用户默认企业的企业id 的表达式
		LambdaQueryWrapper<OrganizationMember> selectDefaultOrgIdQw = new QueryWrapper<OrganizationMember>().lambda()
				.eq(OrganizationMember::getMemberId, ShiroAuthenticationManager.getUserId())
				.eq(OrganizationMember::getUserDefault, true)
				.select(OrganizationMember::getOrganizationId);

		String userDefaultOrganizationId = organizationMemberService.getOne(selectDefaultOrgIdQw).getOrganizationId();
		if(Stringer.isNotNullOrEmpty(userDefaultOrganizationId)){
			myOrg.forEach(item -> {
				if(item.getOrganizationId().equals(userDefaultOrganizationId)){
					item.setIsSelection(true);
				} else {
					item.setIsSelection(false);
				}
			});
		} else {
			myOrg.get(0).setIsSelection(true);
		}
		return myOrg;
	}

	/**
	 * 判断企业是否存在
	 * @param organizationId 企业id
	 * @return 结果
	 */
	@Override
	public Boolean checkOrgIsExist(String organizationId) {
		return organizationMapper.selectCount(new QueryWrapper<Organization>().eq("organization_id", organizationId)) > 0;
	}

	/**
	 * 获取企业下的项目信息
	 * @param orgId 企业id
	 * @return 项目信息集合
	 * @author heShaoHua
	 * @describe 获取企业下的项目信息后,会把当前用户的默认企业改为该企业
	 * @updateInfo 暂无
	 * @date 2019/5/29 10:37
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public List<Project> getProject(String orgId) {
		if(!this.checkOrgIsExist(orgId)){
			return new ArrayList<>();
		}
		//修改用户的默认企业
		int result = organizationMemberService.updateUserDefaultOrg(orgId, ShiroAuthenticationManager.getUserId());
		if(result == -1){
			return new ArrayList<>();
		}
		return organizationMapper.selectProject(orgId);
	}
}