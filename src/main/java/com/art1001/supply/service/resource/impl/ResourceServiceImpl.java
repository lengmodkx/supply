package com.art1001.supply.service.resource.impl;

import com.art1001.supply.entity.resource.ResourceEntity;
import com.art1001.supply.mapper.resource.ResourceMapper;
import com.art1001.supply.service.resource.ResourceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper,ResourceEntity> implements ResourceService {

	@Resource
	private ResourceMapper resourceMapper;

	@Override
	public int deleteResource(Integer resourceId){
		ResourceEntity resourceEntity = resourceMapper.selectById(resourceId);
		int count = resourceMapper.selectCount(new QueryWrapper<>(resourceEntity).eq("s_parent_id",resourceEntity.getResourceId()));
		if(count>0){
			return 0;
		}else{
			return resourceMapper.deleteById(resourceId);
		}
	}

	@Override
	public Page<ResourceEntity> selectListPage(long current, long size, ResourceEntity resourceEntity) {
		Page<ResourceEntity> resourceEntityPage = new Page<>(current,size);
		QueryWrapper<ResourceEntity> queryWrapper = new QueryWrapper<>(resourceEntity);
		return (Page<ResourceEntity>) page(resourceEntityPage, queryWrapper);
	}

	/**
	 * 查询出该角色的所有资源id
	 * @param roleId 角色id
	 */
	@Override
	public List<Integer> listByRoleId(String roleId) {
		return resourceMapper.selectByRoleId(roleId);
	}

	/**
	 * 查询出所有资源 (包括子资源)
	 * @param roleId 角色id
	 * @return
	 */
	@Override
	public List<ResourceEntity> allList(String roleId) {
		return resourceMapper.allList(roleId);
	}
}
