package com.art1001.supply.service.role.impl;

import com.art1001.supply.entity.role.ResourcesRole;
import com.art1001.supply.mapper.role.ResourcesRoleMapper;
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
}
