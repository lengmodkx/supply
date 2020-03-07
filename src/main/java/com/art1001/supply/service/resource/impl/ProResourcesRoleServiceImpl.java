package com.art1001.supply.service.resource.impl;

import com.art1001.supply.entity.resource.ProResourcesRole;
import com.art1001.supply.entity.role.ProRole;
import com.art1001.supply.mapper.resource.ProResourcesRoleMapper;
import com.art1001.supply.service.resource.ProResourceRoleBindTemplateService;
import com.art1001.supply.service.resource.ProResourcesRoleService;
import com.art1001.supply.service.role.ProRoleService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
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

    @Resource
    private RedisUtil redisUtil;

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
        return proResourcesRoleMapper.selectRoleResourceByProjectMember(projectId,memberId);
    }

    @Override
    public Integer saveBatchBind(String orgId) {
        //获取到项目初始化角色的id集合
        List<ProRole> projectInitRoleIds = proRoleService.getProjectInitRoleId(orgId);
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

    @Override
    public Integer distributionRoleResource(Integer roleId, String resources) {
        //构造出查询该角色在角色资源表中存不存在的查询表达式
        LambdaQueryWrapper<ProResourcesRole> resourceRoleCountQw = new QueryWrapper<ProResourcesRole>().lambda()
                .eq(ProResourcesRole::getRId, roleId);

        if (proResourcesRoleMapper.selectCount(resourceRoleCountQw) > 0){
            //构造出更新角色权限的表达式
            LambdaUpdateWrapper<ProResourcesRole> updateRoleResourceUw = new UpdateWrapper<ProResourcesRole>().lambda()
                    .eq(ProResourcesRole::getRId, roleId);
            ProResourcesRole resourcesRole = new ProResourcesRole();
            resourcesRole.setSId(resources);
            proResourcesRoleMapper.update(resourcesRole,updateRoleResourceUw);
        } else {
            //如果该角色没有分配过权限那么就新建一条资源和角色对应的记录
            ProResourcesRole resourcesRole = new ProResourcesRole();
            resourcesRole.setRId(roleId);
            resourcesRole.setSId(resources);
            resourcesRole.setTCreateTime(LocalDateTime.now());
            proResourcesRoleMapper.insert(resourcesRole);
        }

        String userId = ShiroAuthenticationManager.getUserId();
        redisUtil.remove("perms:"+userId);

        return 1;
    }
}
