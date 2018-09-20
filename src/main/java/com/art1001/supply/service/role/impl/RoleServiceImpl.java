package com.art1001.supply.service.role.impl;

import com.art1001.supply.mapper.RoleMapper;
import com.art1001.supply.entity.role.Role;
import com.art1001.supply.service.role.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper,Role> implements RoleService {

}
