package com.art1001.supply.service.role;

import com.art1001.supply.entity.role.ProRole;
import com.art1001.supply.entity.role.ProRoleUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户角色映射表 服务类
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
public interface ProRoleUserService extends IService<ProRoleUser> {

    /**
     * 给用户分配角色
     * @param roleId 角色id
     * @param userId 用户id
     * @param projectId 项目id
     */
    void distributionRoleToUser(Integer roleId, String userId, String projectId);

    /**
     * 查看用户和角色的对应关系是否已经存在
     * @param roleId 角色id
     * @param userId 用户id
     * @return 是否存在
     */
    Boolean checkRelationIsExist(Integer roleId, String userId);

    /**
     * 获取用户在项目中的角色id
     * @param orgId 项目id
     * @param userId 用户id
     * @return 角色信息
     */
    ProRole getRoleIdForProjectUser(String orgId, String userId);

    /**
     * 获取用户在某个企业中的项目角色信息
     * @param orgId 企业id
     * @param userId 用户id
     */
    ProRole getRoleOnOrgForUser(String orgId, String userId);
}
