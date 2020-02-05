package com.art1001.supply.service.resource.impl;

import com.art1001.supply.entity.resource.ProResources;
import com.art1001.supply.entity.resource.ProResourcesRole;
import com.art1001.supply.entity.resource.ResourceShowVO;
import com.art1001.supply.mapper.resource.ProResourcesMapper;
import com.art1001.supply.service.resource.ProResourcesRoleService;
import com.art1001.supply.service.resource.ProResourcesService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 资源表 服务实现类
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
@Service
public class ProResourcesServiceImpl extends ServiceImpl<ProResourcesMapper, ProResources> implements ProResourcesService {

    @Resource
    private ProResourcesRoleService proResourcesRoleService;

    @Resource
    private ProResourcesService proResourcesService;

    @Resource
    private ProResourcesMapper proResourcesMapper;


    @Override
    public List<String> getMemberResourceKey(String orgId, String memberId) {
        String roleResourceByProjectMember = proResourcesRoleService.getRoleResourceByProjectMember(orgId, memberId);
        String[] rIds = roleResourceByProjectMember.split(",");
        return proResourcesService.getResourceKeyByRIds(Arrays.asList(rIds));
    }

    @Override
    public List<String> getResourceKeyByRIds(List<String> rIds) {
        if(CollectionUtils.isEmpty(rIds)){
            return null;
        }
        //构造出根据 resourceId 集合 查询出resourcesKey的sql表达式
        LambdaQueryWrapper<ProResources> selectKeysByRIdsQw = new QueryWrapper<ProResources>().lambda()
                .select(ProResources::getSSourceKey)
                .in(ProResources::getSId, rIds);

        return proResourcesService.list(selectKeysByRIdsQw).stream()
                .map(ProResources::getSSourceKey)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceShowVO> getResourceVO(String roleId) {
        List<ResourceShowVO> allResource = proResourcesMapper.selectAll();
        if(CollectionUtils.isEmpty(allResource)){
            return null;
        }

        //查询出当前角色拥有的权限信息
        List<ProResources> resourcesByRoleId = this.getRoleHaveResources(roleId);
        if(CollectionUtils.isEmpty(resourcesByRoleId)){
            return allResource;
        }

        if(CollectionUtils.isNotEmpty(resourcesByRoleId)){
            //循环比较,构造出ResourceShowVO数据
            allResource.forEach(item -> {
                List<Integer> currSubResources = resourcesByRoleId.stream()
                        //过滤出属于当前资源分组的资源信息
                        .filter(resource -> resource.getSParentId().equals(item.getId()))
                        //提取出上面过滤后的stream中的resourceName字段
                        .map(ProResources::getSId).collect(Collectors.toList());
                item.setCheckAllGroup(currSubResources);
            });
        }
        return allResource;
    }

    @Override
    public List<ProResources> getRoleHaveResources(String roleId) {

        List<String> resourceIds = this.getRoleHaveResourceIds(roleId);
        if(CollectionUtils.isEmpty(resourceIds)){
            return null;
        }

        return proResourcesMapper.selectRoleHaveResources(resourceIds);
    }

    @Override
    public List<String> getRoleHaveResourceIds(String roleId) {
        //生成sql表达式
        LambdaQueryWrapper<ProResourcesRole> selectResourceIdsByRoleId = new QueryWrapper<ProResourcesRole>().lambda()
                .eq(ProResourcesRole::getRId, roleId)
                .select(ProResourcesRole::getSId);
        ProResourcesRole roleResourceByRoleId = proResourcesRoleService.getOne(selectResourceIdsByRoleId);
        if(roleResourceByRoleId == null){
            return new ArrayList<>();
        }
        if(StringUtils.isNotEmpty(roleResourceByRoleId.getSId())){
            return Arrays.asList(roleResourceByRoleId.getSId().split(","));
        }
        return null;
    }
}
