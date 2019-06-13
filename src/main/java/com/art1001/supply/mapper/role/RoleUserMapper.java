package com.art1001.supply.mapper.role;

import com.art1001.supply.entity.role.RoleUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户角色映射表 Mapper 接口
 * </p>
 *
 * @author heshaohua
 * @since 2019-05-28
 */
public interface RoleUserMapper extends BaseMapper<RoleUser> {

    /**
     * 获取到该用户默认企业下拥有的角色
     * @param userId 用户id
     * @param orgId 企业id
     * @return id集合
     */
    List<Integer> selectUserOrgRoleIds(@Param("userId") String userId, @Param("orgId") String orgId);
}
