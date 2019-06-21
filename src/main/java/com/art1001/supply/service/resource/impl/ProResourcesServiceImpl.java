package com.art1001.supply.service.resource.impl;

import com.art1001.supply.entity.resource.ProResources;
import com.art1001.supply.mapper.resource.ProResourcesMapper;
import com.art1001.supply.service.resource.ProResourcesRoleService;
import com.art1001.supply.service.resource.ProResourcesService;
import com.art1001.supply.util.Stringer;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Override
    public List<String> getMemberResourceKey(String projectId, String memberId) {
        if(Stringer.isNullOrEmpty(projectId) || Stringer.isNullOrEmpty(memberId)){
            return null;
        }
        String roleResourceByProjectMember = proResourcesRoleService.getRoleResourceByProjectMember(projectId, memberId);
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
}
