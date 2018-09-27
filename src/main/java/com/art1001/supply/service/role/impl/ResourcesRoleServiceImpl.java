package com.art1001.supply.service.role.impl;

import com.art1001.supply.entity.resource.ResourceEntity;
import com.art1001.supply.entity.role.ResourcesRole;
import com.art1001.supply.mapper.role.ResourcesRoleMapper;
import com.art1001.supply.service.resource.ResourceService;
import com.art1001.supply.service.role.ResourcesRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 角色权限映射表 服务实现类
 * </p>
 *
 * @author 少华
 * @since 2018-09-26
 */
@Service
public class ResourcesRoleServiceImpl extends ServiceImpl<ResourcesRoleMapper, ResourcesRole> implements ResourcesRoleService {

    @Resource
    ResourcesRoleMapper resourcesRoleMapper;

    @Resource
    ResourceService resourceService;

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
                if(resource.getId().equals(haveResources)){
                    resource.setHave(true);
                }
            }
        }
        return allResources;
    }
}
