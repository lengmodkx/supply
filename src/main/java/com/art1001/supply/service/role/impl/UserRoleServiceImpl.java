package com.art1001.supply.service.role.impl;

import com.art1001.supply.entity.role.RoleUser;
import com.art1001.supply.mapper.role.RoleUserMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户与角色对应关系 服务实现类
 * </p>
 *
 * @author DindDangMao
 * @since 2019-01-30
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<RoleUserMapper, RoleUser> implements IService<RoleUser> {



}
