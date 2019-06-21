package com.art1001.supply.service.resource.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.resource.ProResourcesRole;
import com.art1001.supply.entity.role.ProRole;
import com.art1001.supply.mapper.resource.ProResourcesRoleMapper;
import com.art1001.supply.service.resource.ProResourceRoleBindTemplateService;
import com.art1001.supply.service.resource.ProResourcesRoleService;
import com.art1001.supply.service.role.ProRoleService;
import com.art1001.supply.util.Stringer;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 角色权限映射表 服务实现类
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
@Service
public class ProResourcesRoleServiceImpl extends ServiceImpl<ProResourcesRoleMapper, ProResourcesRole> implements ProResourcesRoleService {

    @Resource
    private ProResourcesRoleMapper proResourcesRoleMapper;

    /**
     * 注入项目角色业务层Bean
     */
    @Resource
    private ProRoleService proRoleService;

    /**
     * 注入项目中的角色资源关系模板业务层Bean
     */
    @Resource
    private ProResourceRoleBindTemplateService proResourceRoleBindTemplateService;

    @Override
    public String getRoleResourceByProjectMember(String projectId, String memberId) {
        if(Stringer.isNullOrEmpty(projectId) || Stringer.isNullOrEmpty(memberId)){
            return null;
        }
        return proResourcesRoleMapper.selectRoleResourceByProjectMember(projectId,memberId);
    }

    @Override
    public Integer saveBatchBind(String projectId) {
        if(Stringer.isNullOrEmpty(projectId)){
            return -1;
        }

        //获取到项目初始化角色的id集合
        List<ProRole> projectInitRoleIds = proRoleService.getProjectInitRoleId(projectId);
        if(CollectionUtils.isEmpty(projectInitRoleIds)){
            return -1;
        }

        Map<String, String> roleResourcesTemplateByRoleKey = proResourceRoleBindTemplateService.getRoleResourcesTemplateByRoleKey();

        //构建角色资源对应关系对象并且保存
        projectInitRoleIds.forEach(proRole -> {
            ProResourcesRole proResourcesRole = new ProResourcesRole();
            proResourcesRole.setRId(proRole.getRoleId());
            proResourcesRole.setTCreateTime(LocalDateTime.now());
            proResourcesRole.setSId(roleResourcesTemplateByRoleKey.get(proRole.getRoleKey()));
            proResourcesRoleMapper.insert(proResourcesRole);
        });
        return 1;
    }

}
