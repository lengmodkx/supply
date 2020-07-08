package com.art1001.supply.mapper.role;

import com.art1001.supply.entity.role.ProRoleUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户角色映射表 Mapper 接口
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
public interface ProRoleUserMapper extends BaseMapper<ProRoleUser> {

    Integer getManagersByProject(@Param("projectId") String projectId);

    ProRoleUser findProRoleUser(@Param("projectId")String projectId, @Param("memberId")String memberId);
}
