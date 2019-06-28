package com.art1001.supply.service.role;

import com.art1001.supply.entity.role.ProRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
public interface ProRoleService extends IService<ProRole> {

    /**
     * 初始化项目角色
     * @author heShaoHua
     * @describe 暂无
     * @param projectId 项目id
     * @updateInfo 暂无
     * @date 2019/6/19 15:46
     * @return 是否成功
     */
    Integer initProRole(String projectId);

    /**
     * 获取到项目初始化角色的id和key集合(如果projectId为空则返回null)
     * @author heShaoHua
     * @describe 暂无
     * @param projectId 项目id
     * @updateInfo 暂无
     * @date 2019/6/20 14:22
     * @return id和key集合
     */
    List<ProRole> getProjectInitRoleId(String projectId);

    /**
     * 获取到项目初始化角色的id集合(如果projectId为空则返回null)
     * @author heShaoHua
     * @describe 暂无
     * @param projectId 项目id
     * @updateInfo 暂无
     * @date 2019/6/20 14:22
     * @return id集合
     */
    Integer getDefaultProRoleId(String projectId);

    /**
     * 根据角色key和projectId 获取本项目中的角色id
     * 如果两个参数 任一为空 则返回 -1
     * 如果没有查询结果则返回 -1
     * @author heShaoHua
     * @describe 暂无
     * @param key 角色key
     * @param projectId 项目id
     * @updateInfo 暂无
     * @date 2019/6/20 15:34
     * @return 角色id
     */
    Integer getRoleIdByRoleKey(String key, String projectId);

    /**
     * 添加一个自定义项目角色
     * @author heShaoHua
     * @describe 暂无
     * @param proRole 自定义角色信息
     * @updateInfo 暂无
     * @date 2019/6/20 16:43
     * @return 是否成功
     */
    Integer addProRole(ProRole proRole);

    /**
     * 查询roleKey在当前projectId的项目中存不存在
     * 如果 projectId 和 roleKey 任一为空 则返回-1
     * @author heShaoHua
     * @describe 暂无
     * @param projectId 项目id
     * @param roleKey 角色key
     * @updateInfo 暂无
     * @date 2019/6/20 16:55
     * @return 记录数
     */
    Integer checkIsExist(String projectId, String roleKey);

    /**
     * 根据id删除指定的项目角色
     * @author heShaoHua
     * @describe 暂无
     * @param roleId 角色id
     * @updateInfo 暂无
     * @date 2019/6/21 11:37
     * @return 结果
     */
    Integer removeProRole(Integer roleId, String projectId);

    /**
     * 检查roleId的角色是否为该项目的默认角色
     * 如果roleId为空 则返回false
     * @author heShaoHua
     * @describe 暂无
     * @param roleId 角色id
     * @updateInfo 暂无
     * @date 2019/6/21 12:18
     * @return 结果
     */
    Boolean checkRoleIdIsDefault(Integer roleId);

    /**
     * 设置指定项目中的key角色为该项目的默认角色
     * 两个参数任一为空则返回-1
     * 如果此条roleKey在项目中不存在则返回-1
     * @author heShaoHua
     * @describe 暂无
     * @param projectId 项目id
     * @param roleKey 角色key
     * @updateInfo 暂无
     * @date 2019/6/21 14:00
     * @return 结果
     */
    Integer setProDefaultRole(String projectId, String roleKey);

    /**
     * 获取到该项目中默认的角色id
     * 如果projectId为空则返回-1
     * @author heShaoHua
     * @describe 暂无
     * @param projectId 项目id
     * @updateInfo 暂无
     * @date 2019/6/21 14:11
     * @return roleId
     */
    Integer getProDefaultRoleId(String projectId);

    /**
     * 根据角色id查询该角色存不存在
     * 如果roleId 为空则返回false
     * @author heShaoHua
     * @describe 暂无
     * @param roleId 角色id
     * @updateInfo 暂无
     * @date 2019/6/21 15:26
     * @return 是否存在
     */
    Boolean checkRoleIsExistByRoleId(Integer roleId);

    /**
     * 查询该角色是否是系统初始化的角色
     * 如果roleId为空则返回false
     * @author heShaoHua
     * @describe 暂无
     * @param roleId 角色id
     * @updateInfo 暂无
     * @date 2019/6/21 15:33
     * @return 结果
     */
    Boolean checkRoleIsSystemInit(Integer roleId);

    /**
     * 修改项目角色名称
     * @author heShaoHua
     * @describe 暂无
     * @param roleId 角色id
     * @param roleName 要修改的角色名称
     * @updateInfo 暂无
     * @date 2019/6/26 11:34
     * @return 是否成功
     */
    Integer updateRoleName(Integer roleId, String roleName);

    /**
     * 检查角色是否不存在
     * @author heShaoHua
     * @describe 暂无
     * @param roleId 角色id
     * @updateInfo 暂无
     * @date 2019/6/26 11:52
     * @return  结果
     */
    Boolean checkRoleIsNotExistByRoleId(Integer roleId);

    /**
     * 获取项目下的角色列表
     * @author heShaoHua
     * @describe 暂无
     * @param projectId 项目id
     * @updateInfo 暂无
     * @date 2019/6/26 14:36
     * @return 角色信息集合
     */
    List<ProRole> getProRoles(String projectId);
}
