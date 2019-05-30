package com.art1001.supply.service.resource.impl;

import com.art1001.supply.entity.resource.ResourceRoleBindTemplate;
import com.art1001.supply.mapper.resource.ResourceRoleBindTemplateMapper;
import com.art1001.supply.service.resource.ResourceRoleBindTemplateService;
import com.art1001.supply.util.Stringer;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author heshaohua
 * @since 2019-05-29
 */
@Service
public class ResourceRoleBindTemplateServiceImpl extends ServiceImpl<ResourceRoleBindTemplateMapper, ResourceRoleBindTemplate> implements ResourceRoleBindTemplateService {

    @Resource
    private ResourceRoleBindTemplateMapper resourceRoleBindTemplateMapper;

    @Override
    public String getRoleBindResourceIds(String roleKey) {
        if(Stringer.isNullOrEmpty(roleKey)){
            return null;
        }

        //生成根据角色key查询出该角色对应的资源id字符串(逗号隔开)
        LambdaQueryWrapper<ResourceRoleBindTemplate> selectRoleBindResourceIdQw = new QueryWrapper<ResourceRoleBindTemplate>().lambda()
                .eq(ResourceRoleBindTemplate::getRoleKey, roleKey)
                .select(ResourceRoleBindTemplate::getResourceId);

        //获取到该角色的对应资源id数组
        return resourceRoleBindTemplateMapper.selectOne(selectRoleBindResourceIdQw).getResourceId();
    }
}
