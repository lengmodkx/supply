package com.art1001.supply.mapper.resource;

import com.art1001.supply.entity.resource.ResourceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ResourceMapper extends BaseMapper<ResourceEntity> {
	
	/**
	 * 自定义方法
	 * 获取用户ID对应的资源信息
	 * @param userId
	 * @return
	 */
	public List<ResourceEntity> findResourcesByUserId(@Param(value = "userId") String userId);

	/**
	 * 自定义方法
	 * 获取用户ID对应的资源信息
	 * @param userId
	 * @return
	 */
	public List<ResourceEntity> findResourcesMenuByUserId(@Param(value = "userId") int userId);

	/**
	 * 查询权限树集合
	 * @param parameter 参数中必须包含roleId,其他参数可参考mapping文件
	 * @return
	 */
    public List<ResourceEntity> queryResourceList(Map<String, Object> parameter);

    /**
     *
     * @Description 根据资源id删除角色和资源关联关系
     * @param id
     * @return
     *
     * @author wjggwm
     * @data 2016年11月19日 上午1:21:03
     */
    public int deleteRolePerm(@Param(value = "id") Long id);
    
	/**
	 * 查询权限树集合
	 * @param parameter 参数中必须包含roleId,其他参数可参考mapping文件
	 * @return
	 */
    public List<ResourceEntity> queryTreeGridListByPage(Map<String, Object> parameter);

	/**
	 * 查询出该角色的资源信息
	 * @param roleId 角色id
	 * @return
	 */
	List<Integer> selectByRoleId(String roleId);

	/**
	 * 查询出所有资源 (包括子资源)
	 * @return
	 */
    List<ResourceEntity> allList();
}
