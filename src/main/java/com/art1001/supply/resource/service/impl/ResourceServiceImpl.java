package com.art1001.supply.resource.service.impl;

import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.resource.service.ResourceService;
import com.art1001.supply.base.baseservice.impl.AbstractService;
import com.art1001.supply.resource.mapper.ResourceMapper;
import com.art1001.supply.resource.model.ResourceEntity;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ResourceServiceImpl extends AbstractService<ResourceEntity, Long>
		implements ResourceService {

	@Autowired
	private ResourceMapper resourceMapper;

	// 这句必须要加上。不然会报空指针异常，因为在实际调用的时候不是BaseMapper调用，而是具体的mapper，这里为resourceMapper
	@Autowired
	public void setBaseMapper() {
		super.setBaseMapper(resourceMapper);
	}

	@Override
	public List<ResourceEntity> findResourcesByUserId(int userId) {
		return resourceMapper.findResourcesByUserId(userId);
	}

	@Override
	public List<ResourceEntity> queryResourceList(Map<String, Object> parameter) {
		return resourceMapper.queryResourceList(parameter);
	}

	@Override
	public List<ResourceEntity> findResourcesMenuByUserId(int userId) {
		return resourceMapper.findResourcesMenuByUserId(userId);
	}

	@Override
	public boolean deleteRoleAndResource(List<Long> resourceIds) {
		try
		{
			resourceIds.forEach(resourceId -> {
				resourceMapper.deleteRolePerm(resourceId);
			});
			resourceMapper.deleteBatchById(resourceIds);
			//清空所有用户权限,重新加载权限
			ShiroAuthenticationManager.clearAllUserAuth();
			return true;
		}catch(Exception e)
		{
			throw new ServiceException(e);
		}
	}

	/* (non-Javadoc)
	 * @see com.webside.resource.service.ResourceService#queryTreeGridListByPage(java.util.Map)
	 */
	@Override
	public List<ResourceEntity> queryTreeGridListByPage(Map<String, Object> parameter) {
		return resourceMapper.queryTreeGridListByPage(parameter);
	}

	
}
