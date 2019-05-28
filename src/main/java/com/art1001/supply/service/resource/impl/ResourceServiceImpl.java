package com.art1001.supply.service.resource.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.resource.ResourceEntity;
import com.art1001.supply.entity.resource.ResourceShowVO;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.resource.ResourceMapper;
import com.art1001.supply.service.resource.ResourceService;
import com.art1001.supply.service.role.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper,ResourceEntity> implements ResourceService {

	@Resource
	private ResourceMapper resourceMapper;

	@Resource
	private RoleService roleService;

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

	/**
	 * @param roleId 角色id
	 * @author heShaoHua
	 * @describe 查询出当前角色所对应的权限信息
	 * @updateInfo 暂无
	 * @date 2019/5/27
	 * @return 当前role所拥有的权限信息
	 */
	@Override
	public List<ResourceShowVO> getRoleResourceDetailsData(String roleId) {
		boolean roleNotExist = !roleService.checkIsExist(roleId);
		if(roleNotExist){
			throw new ServiceException("该角色不存在!");
		}
		//分组查询出所有的权限信息
		List<ResourceShowVO> allResources = resourceMapper.selectAll();
		if(CollectionUtils.isEmpty(allResources)){
			return null;
		}

		//查询出当前角色拥有的权限信息
		List<ResourceEntity> resourcesByRoleId = this.getRoleHaveResources(roleId);
		if(CollectionUtils.isNotEmpty(resourcesByRoleId)){
			//循环比较,构造出ResourceShowVO数据
			allResources.forEach(item -> {
				List<String> currSubResources = resourcesByRoleId.stream()
						//过滤出属于当前资源分组的资源信息
						.filter(resource -> resource.getParentId().equals(item.getId()))
						//提取出上面过滤后的stream中的resourceName字段
						.map(ResourceEntity::getResourceName).collect(Collectors.toList());
				item.setCheckAllGroup(currSubResources);
			});
		}
		return allResources;
	}

	/**
	 * 获取该角色的所有资源信息
	 * @param roleId 角色id
	 * @return 资源集合
	 * @author heShaoHua
	 * @describe 暂无
	 * @updateInfo 暂无
	 * @date 2019/5/27
	 */
	@Override
	public List<ResourceEntity> getResourcesByRoleId(String roleId) {
		return resourceMapper.selectResourceByRoleId(roleId);
	}

	/**
	 * 获取该角色拥有的所有资源
	 * @param roleId 角色id
	 * @return 资源集合
	 * @author heShaoHua
	 * @describe 这个资源集合是以分组形式获取的(分组依据为parent资源, 然后一个parent资源对应一组sub资源)
	 * @updateInfo 暂无
	 * @date 2019/5/27
	 */
	@Override
	public List<ResourceEntity> getRoleHaveResources(String roleId) {
		return resourceMapper.selectRoleHaveResources(roleId);
	}
}
