package com.art1001.supply.service.organization.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.role.RoleUser;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.mapper.organization.OrganizationMapper;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.role.ProRoleService;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.service.role.RoleUserService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
	private ProRoleService proRoleService;

	@Resource
	private RoleUserService roleUserService;

	@Resource
	private TaskService taskService;

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
		organizationMemberService.removeMemberByOrgId(organizationId);

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
	public Integer saveOrganization(Organization organization){
		organization.setOrganizationMember(ShiroAuthenticationManager.getUserId());
		organization.setCreateTime(System.currentTimeMillis());
		organization.setUpdateTime(System.currentTimeMillis());
		organizationMapper.insert(organization);

		//添加当前用户为企业拥有者
		organizationMemberService.saveOrgOwnerInfo(organization.getOrganizationId(), ShiroAuthenticationManager.getUserId());

		//初始化默认角色
		roleService.saveOrgDefaultRole(organization.getOrganizationId());

		//初始化企业项目默认角色
		proRoleService.initProRole(organization.getOrganizationId());

		//获取到该企业的拥有者角色id
		Integer orgRoleId = roleService.getOrgRoleIdByKey(organization.getOrganizationId(), Constants.OWNER_KEY);

		RoleUser roleUser = new RoleUser();
		roleUser.setTCreateTime(LocalDateTime.now());
		roleUser.setUId(ShiroAuthenticationManager.getUserId());
		roleUser.setRoleId(orgRoleId);
		roleUser.setOrgId(organization.getOrganizationId());
		roleUserService.save(roleUser);
		return 1;
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
		//构造出 查询当前用户默认企业的企业id 的表达式
		LambdaQueryWrapper<OrganizationMember> selectDefaultOrgIdQw = new QueryWrapper<OrganizationMember>().lambda()
				.eq(OrganizationMember::getMemberId, ShiroAuthenticationManager.getUserId())
				.eq(OrganizationMember::getUserDefault, true)
				.select(OrganizationMember::getOrganizationId);

		OrganizationMember one = organizationMemberService.getOne(selectDefaultOrgIdQw);

		if(myOrg!=null && one!=null){
			String userDefaultOrganizationId = one.getOrganizationId();
			for (Organization item : myOrg) {
				if(item.getOrganizationId().equals(userDefaultOrganizationId)){
					item.setIsSelection(true);
				}
			}
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
		//todo
		if(!this.checkOrgIsExist(orgId)){
			return new ArrayList<>();
		}
		List<Project> myJoinProject = organizationMapper.selectProject(orgId, ShiroAuthenticationManager.getUserId());
		Optional.ofNullable(myJoinProject).ifPresent(projects -> {
			projects.stream().forEach(r->{
				r.setProjectSchedule(automaticUpdateProjectSchedule(r));
			});
		});
		return myJoinProject;
	}

	/**
	 * 自动更新项目进度
	 * @param r
	 */
	@Override
	public Integer automaticUpdateProjectSchedule(Project r) {
		List<Task> taskByProject = taskService.findTaskByProject(r.getProjectId());
		List<Task> collect = taskService.findTaskIsOk(r.getProjectId());
		DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
		float v = ((float) collect.size() / taskByProject.size()) * 100;
		return  (int) v;
	}


	@Override
	public void personalProject(String userId) {
		//构造出 查询当前用户默认企业的企业id 的表达式
		LambdaQueryWrapper<OrganizationMember> selectDefaultOrgIdQw = new QueryWrapper<OrganizationMember>().lambda()
				.eq(OrganizationMember::getMemberId, ShiroAuthenticationManager.getUserId())
				.eq(OrganizationMember::getUserDefault, true)
				.select(OrganizationMember::getOrganizationId);
		OrganizationMember one = organizationMemberService.getOne(selectDefaultOrgIdQw);


		one.setUserDefault(Boolean.FALSE);
		one.setUpdateTime(System.currentTimeMillis());

		organizationMemberService.update(one, new QueryWrapper<OrganizationMember>().lambda().eq(OrganizationMember::getMemberId, userId).eq(OrganizationMember::getOrganizationId,one.getOrganizationId()));
	}

}