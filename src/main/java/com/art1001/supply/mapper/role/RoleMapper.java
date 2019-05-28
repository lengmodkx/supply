package com.art1001.supply.mapper.role;

import com.art1001.supply.entity.role.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 查询出该用户所有
     * @param userId 用户名
     * @return 角色权限信息
     */
    List<Role> selectUserRole(@Param("userId") String userId);
}
