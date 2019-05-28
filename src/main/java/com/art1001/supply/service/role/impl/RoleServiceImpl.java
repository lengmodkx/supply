package com.art1001.supply.service.role.impl;

import com.art1001.supply.entity.role.Role;
import com.art1001.supply.mapper.role.RoleMapper;
import com.art1001.supply.service.role.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper,Role> implements RoleService {

	@Resource
	private RoleMapper roleMapper;

	@Override
	public Page<Role> selectListPage(long current, long size, Role role){
		Page<Role> rolePage = new Page<>(current,size);
		QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
		if(role.getRoleName() == null){
			queryWrapper = new QueryWrapper<Role>().eq("organization_id","0").or(true).eq("organization_id",role.getOrganizationId());
		} else{
			queryWrapper = new QueryWrapper<Role>().and(roleQueryWrapper -> roleQueryWrapper.eq("role_name",role.getRoleName()).or(true).eq("organization_id","0")).eq("organization_id",role.getOrganizationId());
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
}
