package com.art1001.supply.service.resource;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import com.art1001.supply.entity.resource.ResourceEntity;
import com.art1001.supply.entity.resource.ResourceShowVO;
import com.art1001.supply.entity.role.Role;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.annotation.Resource;
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
	 * @param roleId 角色id
	 * @return
	 */
	List<ResourceEntity> allList(String roleId);

	/**
	 * 获取到该角色对应的权限信息
	 * @author heShaoHua
	 * @describe 查询出当前角色所对应的权限信息
	 * @param roleId 角色id
	 * @updateInfo 暂无
	 * @date 2019/5/27
	 * @return 当前role所拥有的权限信息
	 */
	List<ResourceShowVO> getRoleResourceDetailsData(String roleId);

	/**
	 * 获取该角色拥有的所有资源
	 * @author heShaoHua
	 * @describe 这个资源集合是以分组形式获取的(分组依据为parent资源,然后一个parent资源对应一组sub资源)
	 * @param roleId 角色id
	 * @updateInfo 暂无
	 * @date 2019/5/27
	 * @return 资源集合
	 */
	List<ResourceEntity> getRoleHaveResources(String roleId);

	/**
	 * 获取一个角色的资源id集合
	 * @author heShaoHua
	 * @describe 暂无
	 * @param roleId 角色
	 * @updateInfo 暂无
	 * @date 2019/5/30 15:23
	 * @return 资源id集合
	 */
	List<String> getRoleHaveResourceIds(String roleId);

	/**
	 * 查询用户拥有的资源key集合
	 * @param userId 用户id
	 * @param orgId 企业id
	 * @return 资源key集合
	 */
	List<String> getMemberResourceKey(String userId, String orgId);

	/**
	 * 获取用户在该企业中的角色
	 * @param userId 用户id
	 * @param orgId 企业id
	 * @return 角色信息
	 */
	Role getUserRoleInOrg(String userId, String orgId);


    List<String> getResourceKeyByIds(List<String> resourceIdListByRoleId);

}
