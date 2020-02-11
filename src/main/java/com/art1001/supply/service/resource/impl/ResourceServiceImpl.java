package com.art1001.supply.service.resource.impl;

import com.art1001.supply.api.RoleUserApi;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.resource.ResourceEntity;
import com.art1001.supply.entity.resource.ResourceShowVO;
import com.art1001.supply.entity.role.ResourcesRole;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.resource.ResourceMapper;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.resource.ResourceService;
import com.art1001.supply.service.role.ResourcesRoleService;
import com.art1001.supply.service.role.RoleService;
import com.art1001.supply.service.role.RoleUserService;
import com.art1001.supply.util.ValidatedUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper,ResourceEntity> implements ResourceService {

	@Resource
	private ResourceMapper resourceMapper;

	@Resource
    private ResourcesRoleService resourcesRoleService;

	@Resource
	private RoleService roleService;

	@Resource
	private OrganizationService organizationService;

	@Resource
	private OrganizationMemberService organizationMemberService;

	@Resource
	private RoleUserService roleUserService;

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
				List<Integer> currSubResources = resourcesByRoleId.stream()
						//过滤出属于当前资源分组的资源信息
						.filter(resource -> resource.getParentId().equals(item.getId()))
						//提取出上面过滤后的stream中的resourceName字段
						.map(ResourceEntity::getResourceId).collect(Collectors.toList());
				item.setCheckAllGroup(currSubResources);
			});
		}
		return allResources;
	}

	@Override
	public List<ResourceEntity> getRoleHaveResources(String roleId) {
        List<String> roleHaveResourceIds = this.getRoleHaveResourceIds(roleId);
        if(CollectionUtils.isEmpty(roleHaveResourceIds)){
            return new ArrayList<>();
        }
        return resourceMapper.selectRoleHaveResources(roleHaveResourceIds);
	}

    @Override
    public List<String> getRoleHaveResourceIds(String roleId) {
	    //构造出查询该角色资源id
        QueryWrapper<ResourcesRole> selectRoleResourceIdsQw = new QueryWrapper<ResourcesRole>()
                .eq("r_id", roleId)
                .select("r_id as roleId","s_id as resourceId");

        ResourcesRole one = resourcesRoleService.getOne(selectRoleResourceIdsQw);
		List<ResourcesRole> list = resourcesRoleService.list(null);
		if(one == null || one.getResourceId() == null){
            return null;
        }
        return Arrays.asList(one.getResourceId().split(","));
    }

	@Override
	public List<String> getMemberResourceKey(String userId, String orgId) {
		Role userRoleInOrg = this.getUserRoleInOrg(userId, orgId);
		List<String> resourceIdListByRoleId = resourcesRoleService.getResourceIdListByRoleId(userRoleInOrg);

		return this.listByIds(resourceIdListByRoleId)
				   .stream().map(ResourceEntity::getResourceKey).collect(Collectors.toList());
	}

	@Override
	public Role getUserRoleInOrg(String userId, String orgId) {
		ValidatedUtil.filterNullParam(userId, orgId);

		if(organizationService.checkOrgIsExist(orgId)){
			log.info("企业不存在. [{}]", orgId);
//			throw new ServiceException("企业不存在.");
		}

		Optional.ofNullable(organizationMemberService.findOrgByMemberId(userId, orgId))
				.orElseThrow(() -> {
					log.info("用户不在该企业中. [用户id：{}, 企业id：{}]", userId, orgId);
					return new ServiceException("用户不在该企业中。");
				});

		Integer userOrgRoleId = roleUserService.getUserOrgRoleId(userId, orgId);
		Optional.ofNullable(userOrgRoleId)
				.orElseThrow(() -> {
					log.info("用户在企业中没有角色。. [用户id：{}, 企业id：{}]", userId, orgId);
					return new ServiceException("用户在企业中没有角色。");}
				);

		return roleService.getById(userOrgRoleId);
	}
}
