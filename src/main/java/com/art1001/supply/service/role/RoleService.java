package com.art1001.supply.service.role;
import com.art1001.supply.entity.role.Role;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RoleService extends IService<Role> {
    /**
     * 分页查询
     * @param current 当前页
     * @param size 每页多少条数据
     * @param role 查询条件
     * @return
     */
    Page<Role> selectListPage(long current, long size, Role role, String orgId);

    /**
     * 判断角色是否存在
     * @author heShaoHua
     * @describe 暂无
     * @param roleId 角色id
     * @updateInfo 暂无
     * @date 2019/5/27
     * @return 结果 (存在返回true)
     */
    Boolean checkIsExist(String roleId);

    /**
     * 移除企业角色
     * @author heShaoHua
     * @describe 暂无
     * @param roleId 角色id
     * @updateInfo 暂无
     * @date 2019/5/28 12:06
     * @return 是否移除成功
     */
    int removeOrgRole(Integer roleId,String orgId);

    /**
     * 获取orgId企业的默认角色信息
     * 如果orgId为空则返回null
     * @author heShaoHua
     * @describe 暂无
     * @param orgId 企业id
     * @updateInfo 暂无
     * @date 2019/6/3 15:29
     * @return 角色信息
     */
    Role getOrgDefaultRole(String orgId);

    /**
     * 保存企业默认初始化的角色信息
     * @author heShaoHua
     * @describe 暂无
     * @param orgId 企业id
     * @updateInfo 暂无
     * @date 2019/5/28 15:33
     * @return 结果值
     */
    Integer saveOrgDefaultRole(String orgId);

    /**
     * 根据角色key,查询出某个企业的对应角色
     * @author heShaoHua
     * @describe 企业默认角色key说明 拥有者:administrator  管理员:admin 成员:member
     * @param orgId 企业id
     * @param roleKey 角色key
     * @updateInfo 暂无
     * @date 2019/5/29 15:25
     * @return 角色id
     */
    Integer getOrgRoleIdByKey(String orgId, String roleKey);

    /**
     * 更新企业的默认角色
     * @author heShaoHua
     * @describe 暂无
     * @param orgId 企业id
     * @param roleId 角色id
     * @updateInfo 暂无
     * @date 2019/6/3 14:48
     * @return 结果
     */
    Integer updateOrgDefaultRole(String orgId, String roleId);

    /**
     * 获取某个用户的默认企业所有角色信息(角色信息中还包括该角色对应的权限信息)
     * @author heShaoHua
     * @describe 暂无
     * @param userId 用户id
     * @param orgId 企业id
     * @updateInfo 暂无
     * @date 2019/6/4 15:05
     * @return 角色信息集合
     */
    List<Role> getUserOrgRoles(String userId, String orgId);

    /**
     * 获取某个用户的所有角色id集合
     * 如果该用户没有对应的角色那么久返回 size 为 0 的空集合
     * 如果userId为null 则返回null
     * @author heShaoHua
     * @describe 暂无
     * @param userId 用户id
     * @updateInfo 暂无
     * @date 2019/6/4 15:38
     * @return id集合
     */
    List<Integer> getUserOrgRoleIds(String userId, String orgId);

    /**
     * 根据角色id获取到该角色信息以及角色下的权限信息
     * @author heShaoHua
     * @describe 暂无
     * @param roleId 角色id
     * @updateInfo 暂无
     * @date 2019/6/4 15:54
     * @return 角色和角色下的权限信息
     */
    Role getRoleAndResourcesInfo(Integer roleId);

    List<Role> roleForMember(String userId, String orgId);

    /**
     * 根据角色name,查询出某个企业的对应角色
     * @author heShaoHua
     * @describe 企业默认角色key说明 拥有者:administrator  管理员:admin 成员:member
     * @param orgId 企业id
     * @param roleName 角色name
     * @updateInfo 暂无
     * @date 2019/5/29 15:25
     * @return 角色id
     */
    Integer getOrgRoleIdByName(String orgId, String roleName);
}