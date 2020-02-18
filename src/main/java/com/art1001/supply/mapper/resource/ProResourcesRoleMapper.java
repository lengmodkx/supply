package com.art1001.supply.mapper.resource;

import com.art1001.supply.entity.resource.ProResourcesRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 角色权限映射表 Mapper 接口
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
public interface ProResourcesRoleMapper extends BaseMapper<ProResourcesRole> {

    /**
     * 获取到 某个项目成员的权限信息
     * 权限信息:资源id字符串 逗号隔开
     * @param projectId 项目id
     * @param userId 成员id
     * @return 资源id字符串 逗号隔开
     */
    String selectRoleResourceByProjectMember(@Param("projectId") String projectId, @Param("userId") String userId);
}
