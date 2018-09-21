package com.art1001.supply.service.role;
import com.art1001.supply.entity.role.Role;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface RoleService extends IService<Role> {
    /**
     * 分页查询
     * @param current 当前页
     * @param size 每页多少条数据
     * @param role 查询条件
     * @return
     */
    Page<Role> selectListPage(long current, long size, Role role);
}