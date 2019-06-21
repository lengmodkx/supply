package com.art1001.supply.service.resource.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.resource.ProResourceRoleBindTemplate;
import com.art1001.supply.mapper.resource.ProResourceRoleBindTemplateMapper;
import com.art1001.supply.service.resource.ProResourceRoleBindTemplateService;
import com.art1001.supply.util.Stringer;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
@Service
public class ProResourceRoleBindTemplateServiceImpl extends ServiceImpl<ProResourceRoleBindTemplateMapper, ProResourceRoleBindTemplate> implements ProResourceRoleBindTemplateService {

    @Resource
    private ProResourceRoleBindTemplateMapper proResourceRoleBindTemplateMapper;

    @Override
    public Map<String, String> getRoleResourcesTemplateByRoleKey() {
        List<String> roleKeys = new ArrayList<>();
        roleKeys.add(Constants.ADMIN_KEY);
        roleKeys.add(Constants.OWNER_KEY);
        roleKeys.add(Constants.MEMBER_KEY);

        Map<String,String> templateData = new HashMap<>(16);
        roleKeys.forEach(key -> {
            //构造出查询对应roleKey的资源id字符串(多个资源逗号隔开)的sql表达式
            LambdaQueryWrapper<ProResourceRoleBindTemplate> selectRoleResourceTemplateQw = new QueryWrapper<ProResourceRoleBindTemplate>().lambda()
                    .eq(ProResourceRoleBindTemplate::getRoleKey, key)
                    .select(ProResourceRoleBindTemplate::getResourceId);
            String resourceIds = proResourceRoleBindTemplateMapper.selectOne(selectRoleResourceTemplateQw).getResourceId();

            if(Stringer.isNotNullOrEmpty(resourceIds)){
                templateData.put(key, resourceIds);
            }
        });
        return templateData;
    }
}
