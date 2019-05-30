package com.art1001.supply.service.role.impl;

import com.art1001.supply.entity.role.RoleUser;
import com.art1001.supply.mapper.role.RoleUserMapper;
import com.art1001.supply.service.role.RoleUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户角色映射表 服务实现类
 * </p>
 *
 * @author heshaohua
 * @since 2019-05-28
 */
@Service
public class RoleUserServiceImpl extends ServiceImpl<RoleUserMapper, RoleUser> implements RoleUserService {

}
