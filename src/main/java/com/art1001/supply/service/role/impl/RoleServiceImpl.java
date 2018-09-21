package com.art1001.supply.service.role.impl;

import com.art1001.supply.entity.role.Role;
import com.art1001.supply.mapper.role.RoleMapper;
import com.art1001.supply.service.role.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper,Role> implements RoleService {

	@Override
	public Page<Role> selectListPage(long current, long size, Role role){
		Page<Role> rolePage = new Page<>(current,size);
		QueryWrapper<Role> queryWrapper = new QueryWrapper<>(role);
		return (Page<Role>) page(rolePage, queryWrapper);
	}
}
