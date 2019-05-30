package com.art1001.supply.service.role;
import com.art1001.supply.entity.role.Role;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface RoleService extends IService<Role> {
    /**
     * 分页查询
     * @param current 当前页
     * @param size 每页多少条数据
     * @param role 查询条件
     * @return
     */
    Page<Role> selectListPage(long current, long size, Role role);

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
}