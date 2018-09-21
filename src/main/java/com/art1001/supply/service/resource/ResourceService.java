package com.art1001.supply.service.resource;

import com.art1001.supply.entity.resource.ResourceEntity;
import com.art1001.supply.entity.role.Role;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
