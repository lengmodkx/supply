package com.art1001.supply.service.role.impl;

import com.art1001.supply.entity.resource.ResourceEntity;
import com.art1001.supply.entity.role.ResourcesRole;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.role.ResourcesRoleMapper;
import com.art1001.supply.service.resource.ResourceRoleBindTemplateService;
import com.art1001.supply.service.resource.ResourceService;
import com.art1001.supply.service.role.ResourcesRoleService;
import com.art1001.supply.service.role.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 角色权限映射表 服务实现类
 * </p>
 * @author 少华
 * @since 2018-09-26
 */
@Service
public class ResourcesRoleServiceImpl extends ServiceImpl<ResourcesRoleMapper, ResourcesRole> implements ResourcesRoleService {

    @Resource
    private ResourcesRoleMapper resourcesRoleMapper;

    @Resource
    private RoleService roleService;

    @Resource
    private ResourceService resourceService;

    @Resource
    private ResourceRoleBindTemplateService resourceRoleBindTemplateService;

    /**
     * 处理编辑角色资源数据
     * 1.获取该角色所有的资源id
     * 2.获取所有资源数据 循环判断 该资源是否是当前角色所拥有的
     * @param roleId 角色id
     * @return
     */
    @Override
    public List<ResourceEntity> showRoleResources(String roleId) {
        List<Integer> roleHaveResource = resourceService.listByRoleId(roleId);
        List<ResourceEntity> allResources = resourceService.list(null);
        for (ResourceEntity resource : allResources) {
            for (Integer haveResources : roleHaveResource) {
                if(resource.getResourceId().equals(haveResources)){
                    resource.setHave(true);
                }
            }
        }
        return allResources;
    }

    /**
     * 把该企业的默认角色和资源进行关系绑定
     * @param orgId 企业id
     * @return 结果
     * @author heShaoHua
     * @describe 暂无
     * @updateInfo 暂无
     * @date 2019/5/29 15:52
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer saveBatch(String orgId) {
        //构造查询企业默认角色的条件查询器
        LambdaQueryWrapper<Role> selectOrgInitRoleIdQw = new QueryWrapper<Role>().lambda()
                .eq(Role::getOrganizationId, orgId)
                .eq(Role::getIsSystemInit, true)
                .select(Role::getRoleId,Role::getRoleKey);

        //查询并提取出系统默认角色的id集合
        List<Role> orgInitRoleIds = roleService.list(selectOrgInitRoleIdQw);

        orgInitRoleIds.forEach(r -> {
            ResourcesRole resourcesRole = new ResourcesRole();
            resourcesRole.setRoleId(r.getRoleId());
            resourcesRole.setCreateTime(LocalDateTime.now());
            String resources = resourceRoleBindTemplateService.getRoleBindResourceIds(r.getRoleKey());
            resourcesRole.setResourceId(resources);
            resourcesRoleMapper.insert(resourcesRole);
        });
        return 1;
    }

    @Override
    public Integer distributionRoleResource(Integer roleId, String resourceIds) {
        //构造出查询该角色在角色资源表中存不存在的查询表达式
        LambdaQueryWrapper<ResourcesRole> resourceRoleCountQw = new QueryWrapper<ResourcesRole>().lambda()
                .eq(ResourcesRole::getRoleId, roleId);

        if (resourcesRoleMapper.selectCount(resourceRoleCountQw) > 0){
            //构造出更新角色权限的表达式
            LambdaUpdateWrapper<ResourcesRole> updateRoleResourceUw = new UpdateWrapper<ResourcesRole>().lambda()
                    .eq(ResourcesRole::getRoleId, roleId);
            ResourcesRole resourcesRole = new ResourcesRole();
            resourcesRole.setResourceId(resourceIds);
            resourcesRoleMapper.update(resourcesRole,updateRoleResourceUw);
        } else {
            //如果该角色没有分配过权限那么就新建一条资源和角色对应的记录
            ResourcesRole resourcesRole = new ResourcesRole();
            resourcesRole.setRoleId(roleId);
            resourcesRole.setResourceId(resourceIds);
            resourcesRole.setCreateTime(LocalDateTime.now());
            resourcesRoleMapper.insert(resourcesRole);
        }
        return 1;
    }

    @Override
    public List<String> getResourceIdListByRoleId(Role role) {
        Optional.ofNullable(role).orElseThrow(() -> new ServiceException("角色id不能为空!"));

        LambdaQueryWrapper<ResourcesRole> eq = new QueryWrapper<ResourcesRole>().
                lambda().eq(ResourcesRole::getRoleId, role.getRoleId());

        ResourcesRole resourcesRole = this.getOne(eq);

        Optional<String> resources = Optional.ofNullable(resourcesRole).map(ResourcesRole::getResourceId);

        return resources.map(s -> Arrays.asList(s.split(","))).orElseGet(LinkedList::new);

    }
}
