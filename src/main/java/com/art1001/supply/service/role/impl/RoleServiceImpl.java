package com.art1001.supply.service.role.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.entity.role.RoleUser;
import com.art1001.supply.mapper.role.RoleMapper;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.role.ResourcesRoleService;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.service.role.RoleUserService;
import com.art1001.supply.util.Stringer;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
		//创建条件表达式
		LambdaQueryWrapper<RoleUser> eq = new QueryWrapper<RoleUser>().lambda().eq(RoleUser::getRoleId, roleId);
		List<String> userIds = roleUserService.list(eq).stream().map(RoleUser::getUId).collect(Collectors.toList());
		Role role = roleMapper.selectById(roleId);
		Integer defaultRoleId;
		if(role.getIsDefault()){
			Role updateRole = new Role();
			defaultRoleId = roleMapper.selectOne(new QueryWrapper<Role>().lambda().eq(Role::getOrganizationId, orgId).eq(Role::getRoleKey, Constants.MEMBER_EN)).getRoleId();
			updateRole.setRoleId(defaultRoleId);
			updateRole.setIsDefault(true);
			roleMapper.updateById(updateRole);
		} else {
			defaultRoleId= roleMapper.selectOne(new QueryWrapper<Role>().lambda().eq(Role::getOrganizationId, orgId).eq(Role::getIsDefault, true).select(Role::getRoleId)).getRoleId();
		}
		List<RoleUser> roleUsers = new ArrayList<>();
		for (String userId : userIds) {
			RoleUser roleUser = new RoleUser();
			roleUser.setRoleId(defaultRoleId);
			roleUser.setUId(userId);
			roleUser.setTCreateTime(LocalDateTime.now());
			roleUsers.add(roleUser);
		}
		roleUserService.remove(new QueryWrapper<RoleUser>().lambda().in(RoleUser::getUId,userIds));
		roleUserService.saveBatch(roleUsers);
		return roleMapper.deleteById(roleId);
	}

	/**
	 * 保存企业默认初始化的角色信息
	 * @param orgId 企业id
	 * @return 结果值
	 * @author heShaoHua
	 * @describe 暂无
	 * @updateInfo 暂无
	 * @date 2019/5/28 15:33
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Integer saveOrgDefaultRole(String orgId) {
		if(Stringer.isNullOrEmpty(orgId)){
			return -1;
		}
		if(!organizationService.checkOrgIsExist(orgId)){
			return -1;
		}
		//这里手动添加默认角色信息,后期可以读取配置文件进行添加
		String[] initRoles = {"拥有者","管理员","成员"};
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

	/**
	 * 根据角色key,查询出某个企业的对应角色
	 * @param orgId   企业id
	 * @param roleKey 角色key
	 * @return 角色id
	 * @author heShaoHua
	 * @describe 企业默认角色key说明 拥有者:administrator  管理员:admin 成员:member
	 * @updateInfo 暂无
	 * @date 2019/5/29 15:25
	 */
	@Override
	public Integer getOrgRoleIdByKey(String orgId, String roleKey) {
		if(Stringer.isNullOrEmpty(orgId) || Stringer.isNullOrEmpty(roleKey)){
			return -1;
		}
		//构造出查询该企业超级管理员id的条件表达式
		LambdaQueryWrapper<Role> selectAdministratorId = new QueryWrapper<Role>().lambda()
				.eq(Role::getOrganizationId, orgId)
				.eq(Role::getRoleKey, "administrator");
		return roleMapper.selectOne(selectAdministratorId).getRoleId();
	}
}
