package com.art1001.supply.service.resource;

import com.art1001.supply.entity.resource.ResourceEntity;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ResourceService extends IService<ResourceEntity> {
	int deleteResource(Integer resourceId);

	/**
	 * 分页查询
	 * @param current 当前页
	 * @param size 每页多少条数据
	 * @param resourceEntity 查询条件
	 * @return
	 */
	Page<ResourceEntity> selectListPage(long current, long size, ResourceEntity resourceEntity);

	/**
	 * 查询出该角色的所有资源id
	 * @param roleId 角色id
	 */
    List<Integer> listByRoleId(String roleId);

	/**
	 * 查询出所有资源 (包括子资源)
	 * @return
	 */
	List<ResourceEntity> allList();
}
