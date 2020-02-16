package com.art1001.supply.service.role.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.role.ProRole;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.entity.role.RoleUser;
import com.art1001.supply.mapper.role.RoleMapper;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.role.ResourcesRoleService;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.service.role.RoleUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author heshaohua
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper,Role> implements RoleService {

	@Resource
	private RoleMapper roleMapper;

	@Resource
	private RoleUserService roleUserService;

	@Resource
	private OrganizationService organizationService;

	@Resource
	private ResourcesRoleService resourcesRoleService;

	@Override
	public Page<Role> selectListPage(long current, long size, Role role, String orgId){
		Page<Role> rolePage = new Page<>(current,size);
		QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
		if(role.getRoleName() == null){
			queryWrapper = new QueryWrapper<Role>().eq("organization_id",role.getOrganizationId());
		} else{
			queryWrapper = new QueryWrapper<Role>()
					.and(roleQueryWrapper -> roleQueryWrapper
							.eq("role_name",role.getRoleName())
							.or(true).eq("organization_id","0"))
					.eq("organization_id",role.getOrganizationId());
		}
		return (Page<Role>) page(rolePage, queryWrapper);
	}

	/**
	 * 判断角色是否存在
	 * @author heShaoHua
	 * @describe 暂无
	 * @param roleId 角色id
	 * @updateInfo 暂无
	 * @date 2019/5/27
	 * @return 结果
	 */
	@Override
	public Boolean checkIsExist(String roleId) {
		return roleMapper.selectCount(new QueryWrapper<Role>().lambda().eq(Role::getRoleId, roleId)) > 0;
	}

	/**
	 * 移除企业角色
	 * 1.查询出这个角色对应的用户信息
	 * 2.将查询出来的用户信息的对应角色设置为该企业的默认角色
	 * 3.如果当前删除的角色信息正是该企业的默认角色那么删除后就要将该企业的 "成员角色" 设置为默认角色
	 * @param roleId 角色id
	 * @return 是否移除成功
	 * @author heShaoHua
	 * @describe 暂无
	 * @updateInfo 暂无
	 * @date 2019/5/28 12:06
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int removeOrgRole(Integer roleId,String orgId) {
		//构造出查询当前roleId角色对用的用户id条件表达式
		LambdaQueryWrapper<RoleUser> eq = new QueryWrapper<RoleUser>().lambda().eq(RoleUser::getRoleId, roleId);
		List<String> userIds = roleUserService.list(eq).stream().map(RoleUser::getUId).collect(Collectors.toList());
		Role role = roleMapper.selectById(roleId);
		Integer defaultRoleId;

		//如果roleId是当前企业的默认角色,那么该角色被删除后就要把orgId的默认角色设为roleKey为 "member" 的角色
		if(role.getIsDefault()){
			Role updateRole = new Role();
			defaultRoleId = this.getOrgRoleIdByKey(orgId,Constants.MEMBER_KEY);
			updateRole.setRoleId(defaultRoleId);
			updateRole.setIsDefault(true);
			roleMapper.updateById(updateRole);
		} else {
			//获取到当前企业的默认角色id
			defaultRoleId= this.getOrgDefaultRole(orgId).getRoleId();
		}

		//循环插入用户角色关系信息
		for (String userId : userIds) {
			RoleUser roleUser = new RoleUser();
			roleUser.setRoleId(defaultRoleId);
			roleUser.setUId(userId);
			roleUser.setTCreateTime(LocalDateTime.now());
			roleUserService.save(roleUser);
		}

		//移除原来的角色用户关系信息
		roleUserService.remove(new QueryWrapper<RoleUser>().lambda().in(RoleUser::getUId,userIds));
		//删除角色
		return roleMapper.deleteById(roleId);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Integer saveOrgDefaultRole(String orgId) {

		if(!organizationService.checkOrgIsExist(orgId)){
			return -1;
		}
		//这里手动添加默认角色信息,后期可以读取配置文件进行添加
		String[] initRoles = {"拥有者","管理员","成员","外部成员"};
		for (String roleName : initRoles) {
			Role role = new Role();
			role.setRoleName(roleName);
			switch (roleName){
				case Constants.OWNER_CN:
					role.setRoleKey("administrator");
					role.setRoleDes("企业的拥有者");
					break;
				case Constants.ADMIN_CN:
					role.setRoleKey("admin");
					role.setRoleDes("企业的管理者");
					break;
				case Constants.EXTERNAL:
					role.setRoleKey("externalMember");
					role.setRoleDes("企业外部成员");
				default:
					role.setRoleKey("member");
					role.setIsDefault(true);
					role.setRoleDes("企业的普通成员");
					break;
			}
			role.setOrganizationId(orgId);
			role.setCreateTime(new Timestamp(System.currentTimeMillis()));
			role.setUpdateTime(new Timestamp(System.currentTimeMillis()));
			role.setIsSystemInit(true);
			roleMapper.insert(role);
		}
		resourcesRoleService.saveBatch(orgId);
		return 1;
	}

	@Override
	public Integer getOrgRoleIdByKey(String orgId, String roleKey) {

		//构造出查询该企业超级管理员id的条件表达式
		LambdaQueryWrapper<Role> selectAdministratorId = new QueryWrapper<Role>().lambda()
				.eq(Role::getOrganizationId, orgId)
				.eq(Role::getRoleKey, roleKey);
		return roleMapper.selectOne(selectAdministratorId).getRoleId();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Integer updateOrgDefaultRole(String orgId, String roleId) {
		//取消掉该企业下所有的默认企业
		LambdaUpdateWrapper<Role> cancelAllDefaultRoleUw = new UpdateWrapper<Role>().lambda().eq(Role::getOrganizationId, orgId);
		Role canDefaultRole = new Role();
		canDefaultRole.setIsDefault(false);
		roleMapper.update(canDefaultRole,cancelAllDefaultRoleUw);

		//设置roleId的角色为orgId企业的默认角色
		Role setDefaultRole = new Role();
		setDefaultRole.setIsDefault(true);
		//追加设置该企业下默认角色的条件
		cancelAllDefaultRoleUw.eq(Role::getRoleId, roleId);
		roleMapper.update(setDefaultRole,cancelAllDefaultRoleUw);
		return 1;
	}

	@Override
	public Role getOrgDefaultRole(String orgId) {

		LambdaQueryWrapper<Role> selectOrgDefaultRoleQw = new QueryWrapper<Role>().lambda().eq(Role::getOrganizationId, orgId).eq(Role::getIsDefault, true);
		return roleMapper.selectOne(selectOrgDefaultRoleQw);
	}

	@Override
	public List<Role> getUserOrgRoles(String userId, String orgId) {
		List<Integer> userRoleIds = this.getUserOrgRoleIds(userId,orgId);
		if(CollectionUtils.isEmpty(userRoleIds)){
			return null;
		}

		List<Role> userRoles = new ArrayList<>();
		//依次查询出该角色信息以及该角色对应的权限信息
		userRoleIds.forEach(roleId -> {
			userRoles.add(this.getRoleAndResourcesInfo(roleId));
		});
		return userRoles;
	}

	@Override
	public List<Integer> getUserOrgRoleIds(String userId, String orgId) {
		List<Integer> roles = roleUserService.getUserOrgRoleIds(userId,orgId);
		if(CollectionUtils.isEmpty(roles)){
			return new ArrayList<>();
		} else {
			return roles;
		}
	}

	/**
	 * 根据角色id获取到该角色信息以及角色下的权限信息
	 * @param roleId 角色id
	 * @return 角色和角色下的权限信息
	 * @author heShaoHua
	 * @describe 暂无
	 * @updateInfo 暂无
	 * @date 2019/6/4 15:54
	 */
	@Override
	public Role getRoleAndResourcesInfo(Integer roleId) {
		return roleMapper.selectRoleAndResrouceInfo(roleId);
	}

	@Override
	public List<Role> roleForMember(String userId, String orgId) {
		List<Role> roles = list(new QueryWrapper<Role>().eq("organization_id",orgId));
		roles.forEach(role -> {
			RoleUser roleUser = roleUserService.getOne(new QueryWrapper<RoleUser>().eq("u_id",userId).eq("org_id",orgId));
			if(roleUser != null){
				if(roleUser.getRoleId().equals(role.getRoleId())){
					role.setCurrentCheck(true);
				} else {
					role.setCurrentCheck(false);
				}
			}
		});
		return roles;
	}
}
