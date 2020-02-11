package com.art1001.supply.service.role;

import com.art1001.supply.entity.resource.ResourceEntity;
import com.art1001.supply.entity.role.ResourcesRole;
import com.art1001.supply.entity.role.Role;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 角色权限映射表 服务类
 * </p>
 *
 * @author 少华
 * @since 2018-09-26
 */
public interface ResourcesRoleService extends IService<ResourcesRole> {

    /**
     * 处理编辑角色权限数据
     * @param roleId 角色id
     * @return
     */
    List<ResourceEntity> showRoleResources(String roleId);

    /**
     * 把该企业的默认角色和资源进行关系绑定
     * @author heShaoHua
     * @describe 暂无
     * @param orgId 企业id
     * @updateInfo 暂无
     * @date 2019/5/29 15:52
     * @return 结果
     */
    Integer saveBatch(String orgId);

    /**
     * 给一个角色分配权限
     * @author heShaoHua
     * @describe 暂无
     * @param roleId 角色id
     * @param resources 资源id (多个逗号隔开)
     * @updateInfo 暂无
     * @date 2019/5/30 17:18
     * @return 结果
     */
    Integer distributionRoleResource(Integer roleId, String resources);

    /**
     * 根据角色id获取资源id集合
     * @param roleId 角色id
     * @return 资源id集合
     */
    List<String> getResourceIdListByRoleId(Role roleId);
}
