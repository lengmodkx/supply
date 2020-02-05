package com.art1001.supply.mapper.role;

import com.art1001.supply.entity.role.ProRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
public interface ProRoleMapper extends BaseMapper<ProRole> {

    /**
     * 获取项目下的角色列表
     * @param projectId 项目id
     * @return 角色信息集合
     */
    List<ProRole> selectProRoles(@Param("orgId") String orgId);
}
