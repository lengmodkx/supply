package com.art1001.supply.service.role;


import com.art1001.supply.entity.role.RoleEntity;

import java.util.List;
import java.util.Map;

public interface RoleService {

	public List<RoleEntity> queryListByPage(Map<String, Object> parameter);

	public RoleEntity findByName(String name);
	
	public int insert(RoleEntity roleEntity);
	
	public RoleEntity findById(Long roleId);

	public int update(RoleEntity roleEntity);
    
    public int deleteBatchById(List<Long> roleIds);
    
    public boolean deleteRoleById(Long roleId);
    
    public boolean addRolePermBatch(int roleId, List<Integer> ids);
    
    public boolean addRolePerm(Long roleId, Long resourceId);

    public int findRoleUserById(int roleId);
}