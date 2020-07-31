package com.art1001.supply.service.resource.impl;

import com.art1001.supply.entity.resource.ProResourcesRole;
import com.art1001.supply.entity.role.ProRole;
import com.art1001.supply.mapper.resource.ProResourcesRoleMapper;
import com.art1001.supply.service.project.OrganizationMemberService;
import com.art1001.supply.service.resource.ProResourceRoleBindTemplateService;
import com.art1001.supply.service.resource.ProResourcesRoleService;
import com.art1001.supply.service.resource.ProResourcesService;
import com.art1001.supply.service.role.ProRoleService;
import com.art1001.supply.service.role.ProRoleUserService;
import com.art1001.supply.util.RedisUtil;
import com.art1001.supply.util.ValidatedUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Resource
    private ProRoleUserService proRoleUserService;

    @Resource
    private ProResourcesService proResourcesService;

    @Resource
    private ProResourcesRoleService proResourcesRoleService;

    @Resource
    private OrganizationMemberService organizationMemberService;

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
    public void distributionRoleResource(Integer roleId, String resources,String orgId) {
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
        ProRole byId = proRoleService.getById(roleId);
        this.refreshRedisResourceKey(byId.getRoleId(), byId.getOrgId());
    }


    private void refreshRedisResourceKey(Integer roleId, String orgId){
        List<String> userIdList = proRoleUserService.getRoleUserIdListByRoleId(roleId);
        for (String userId : userIdList) {
            String userDefaultOrgId = organizationMemberService.findOrgByUserId(userId);
            //如果用户没有默认企业用户id，则不进行操作
            if(StringUtils.isEmpty(userDefaultOrgId)){
                continue;
            }
            if(userDefaultOrgId.equals(orgId)){
                //移除掉旧的用户项目权限
                redisUtil.remove("perms:" + userId);

                List<String> resourceKeyByRIds = proResourcesService.getResourceKeyByRIds(
                        proResourcesRoleService.getResourceIdsByRoleId(roleId)
                );
                //获取到新的权限并且set到redis中
                redisUtil.lset("perms:" + userId, resourceKeyByRIds);

            }
        }

    }

    @Override
    public List<String> getResourceIdsByRoleId(Integer roleId) {
        ValidatedUtil.filterNullParam(roleId);

        LambdaQueryWrapper<ProResourcesRole> eq = new QueryWrapper<ProResourcesRole>()
                .lambda().eq(ProResourcesRole::getRId, roleId);

        ProResourcesRole proResourcesRole = this.getOne(eq);

        //获取逗号隔开的id串
        String sId = Optional.of(proResourcesRole).map(ProResourcesRole::getSId).get();

        return Arrays.asList(sId.split(","));
    }
}
