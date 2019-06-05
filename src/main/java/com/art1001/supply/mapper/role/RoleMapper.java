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

    /**
     * 根据角色id获取到该角色信息以及角色下的权限信息
     * @author heShaoHua
     * @describe 暂无
     * @param roleId 角色id
     * @updateInfo 暂无
     * @date 2019/6/4 16:03
     * @return 角色以及角色资源信息
     */
    Role selectRoleAndResrouceInfo(@Param("roleId") Integer roleId);
}
