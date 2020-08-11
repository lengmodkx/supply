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

    /**
     * 将拥有者的权限变为成员
     * @param ownerId 拥有者id
     * @param orgId 企业id
     * @param  roleId 成员权限id
     * @return 角色id
     */
    Boolean updateRoleOwner(@Param("orgId")String orgId, @Param("ownerId")String ownerId, @Param("roleId")Integer roleId);
    /**
     * 将成员的权限变为拥有者
     * @param roleId 拥有者权限id
     * @param orgId 企业id
     * @param  memberId 成员id
     * @return 角色id
     */
    Boolean updateRoleMember(@Param("orgId")String orgId,  @Param("memberId")String memberId,@Param("roleId")Integer roleId);


    /**
     * 获取企业拥有者
     * @param orgId
     * @return
     */
    RoleUser getOrgOwenr(@Param("orgId")String orgId);
}
