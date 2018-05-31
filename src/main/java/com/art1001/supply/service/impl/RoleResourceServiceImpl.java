package com.art1001.supply.service.impl;

import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.resource.service.ResourceService;
import com.art1001.supply.role.model.RoleEntity;
import com.art1001.supply.role.service.RoleService;
import com.art1001.supply.service.RoleResourceService;
import com.art1001.supply.resource.model.ResourceEntity;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleResourceServiceImpl implements RoleResourceService {

	@Autowired
	private RoleService roleService;
	
	@Autowired
	private ResourceService resourceService;
	
	@Override
	public boolean insertRoleAndResource(ResourceEntity resourceEntity) {
		try
		{
			//1、添加资源
			resourceService.insert(resourceEntity);
			//2、超级管理员直接赋予该权限
			RoleEntity role = roleService.findByName("超级管理员");
			roleService.addRolePerm(role.getId(), resourceEntity.getId());
			//清空所有用户权限,重新加载权限
			ShiroAuthenticationManager.clearAllUserAuth();
			return true;
		}catch(Exception e)
		{
			throw new ServiceException(e);
		}
	}

}
