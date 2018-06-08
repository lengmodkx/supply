package com.art1001.supply.service.resource;

import com.art1001.supply.entity.resource.ResourceEntity;

import java.util.List;
import java.util.Map;

public interface ResourceService {

	/**
	 * 自定义方法
	 * 获取用户ID对应的资源信息
	 * @param userId
	 * @return
	 */
	public List<ResourceEntity> findResourcesByUserId(String userId);

	/**
	 * 自定义方法
	 * 获取用户ID对应的资源菜单信息
	 * @param userId
	 * @return
	 */
	public List<ResourceEntity> findResourcesMenuByUserId(int userId);
	
	public List<ResourceEntity> queryListByPage(Map<String, Object> parameter);
	
	public List<ResourceEntity> queryTreeGridListByPage(Map<String, Object> parameter);
	
	public ResourceEntity findByName(String name);
	
	public ResourceEntity findById(Long id);

	public int update(ResourceEntity resourceEntity);
    
    public int deleteBatchById(List<Long> resourceIds);
    
    public List<ResourceEntity> queryResourceList(Map<String, Object> parameter);
    
    public int insert(ResourceEntity resourceEntity);
    
    public int count(Map<String, Object> parameter);
    
    public boolean deleteRoleAndResource(List<Long> resourceIds);

	/**
	 *
	 * @Title: insertRoleAndResource
	 * @Description: 添加资源(权限)时同步给超级管理员赋予该权限
	 * @param resourceEntity
	 * @return	boolean
	 * @throws
	 */
	public boolean insertRoleAndResource(ResourceEntity resourceEntity);
    
}
