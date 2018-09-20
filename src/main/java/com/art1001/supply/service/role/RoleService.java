package com.art1001.supply.service.role;


import com.art1001.supply.entity.role.RoleEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.management.relation.Role;
import java.util.List;
import java.util.Map;

public interface RoleService extends IService<RoleEntity> {

	 List<RoleEntity> queryListByPage(Map<String, Object> parameter);

	 RoleEntity findByName(String name);
	
	 int insert(RoleEntity roleEntity);
	
	 RoleEntity findById(Long roleId);

	 int update(RoleEntity roleEntity);
    
     int deleteBatchById(List<Long> roleIds);
    
     boolean deleteRoleById(Long roleId);
    
     boolean addRolePermBatch(int roleId, List<Integer> ids);
    
     boolean addRolePerm(Long roleId, Long resourceId);

     int findRoleUserById(int roleId);
}