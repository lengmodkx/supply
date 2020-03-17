package com.art1001.supply.service.role;

import com.art1001.supply.entity.role.RoleUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户角色映射表 服务类
 * </p>
 *
 * @author heshaohua
 * @since 2019-05-28
 */
public interface RoleUserService extends IService<RoleUser> {

    /**
     * 获取到该用户默认企业下拥有的角色
     * @param userId 用户id
     * @param orgId 企业id
     * @return 用户id
     */
    List<Integer> getUserOrgRoleIds(String userId, String orgId);

    /**
     * 获取用户在企业下的角色id
     * @param userId 用户id
     * @param orgId 企业id
     * @return 角色id
     */
    Integer getUserOrgRoleId(String userId, String orgId);

    /**
     * 将拥有者的权限变为成员
     * @param ownerId 拥有者id
     * @param orgId 企业id
     * @param  memberId 成员id
     * @return 角色id
     */
    Boolean updateRoleTransfer(String orgId, String ownerId, String memberId);

    List<String> getRoleUserIdListByRoleId(Integer roleId);
}
