package com.art1001.supply.service.role.impl;

import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.role.RoleMapper;
import com.art1001.supply.entity.role.RoleEntity;
import com.art1001.supply.service.base.impl.AbstractService;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleServiceImpl extends AbstractService<RoleEntity, Long> implements RoleService {

	@Resource
	private RoleMapper roleMapper;

	// 这句必须要加上。不然会报空指针异常，因为在实际调用的时候不是BaseMapper调用，而是具体的mapper，这里为userMapper
	public void setBaseMapper() {
		super.setBaseMapper(roleMapper);
	}

	public RoleServiceImpl(){
        super.setBaseMapper(roleMapper);
    }


	@Override
	public boolean addRolePermBatch(int id, List<Integer> ids) {
		boolean flag = false;
		try {
			int permCount = roleMapper.findRoleResourceById(id);
			boolean delFlag = true;
			if (permCount > 0) {
				int delResult = roleMapper.deleteRoleResource(id);
				if (permCount != delResult) {
					delFlag = false;
				}
			}

			if (delFlag) {
				if (ids.size() > 0) {
					Map<String, Object> parameter = new HashMap<String, Object>();
					parameter.put("roleId", id);
					parameter.put("resourceIds", ids);
					int addResult = roleMapper.addRoleResourceBatch(parameter);
					if (addResult == ids.size()) {
						flag = true;
					}
				} else {
					flag = true;
				}
			}
			
			List<Long> userIds = roleMapper.findUserIdByRoleId(id);
			ShiroAuthenticationManager.clearUserAuthByUserId(userIds);
			
			return flag;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean deleteRoleById(Long id) {
		try {
			// 1、删除该角色的权限信息
			roleMapper.deleteRoleResource(id.intValue());
			// 2、删除该角色
			if (roleMapper.deleteById(id) > 0) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public int findRoleUserById(int roleId) {
		return roleMapper.findRoleUserById(roleId);
	}

	@Override
	public boolean addRolePerm(Long roleId, Long resourceId) {
		try {
			Map<String, Object> parameter = new HashMap<String, Object>();
			parameter.put("roleId", roleId);
			parameter.put("resourceId", resourceId);
			return roleMapper.addRoleResource(parameter) > 0;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

}
