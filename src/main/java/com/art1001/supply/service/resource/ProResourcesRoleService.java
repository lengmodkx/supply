package com.art1001.supply.service.resource;

import com.art1001.supply.entity.resource.ProResourcesRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 角色权限映射表 服务类
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
public interface ProResourcesRoleService extends IService<ProResourcesRole> {


    /**
     * 获取到 某个项目成员的权限信息
     * 权限信息:资源id字符串 逗号隔开
     * 如果两个参数其中任何一个为空 则返回null
     * @author heShaoHua
     * @describe 暂无
     * @param projectId 项目id
     * @param memberId 用户id
     * @updateInfo 暂无
     * @date 2019/6/19 17:19
     * @return 资源id字符串
     */
    String getRoleResourceByProjectMember(String projectId, String memberId);

    /**
     * 保存项目初始化的角色资源关系信息
     * 如果projectId为空的话则返回-1
     * 如果该项目没有初始化角色则返回-1
     * @author heShaoHua
     * @describe 暂无
     * @param projectId 项目id
     * @updateInfo 暂无
     * @date 2019/6/20 14:19
     * @return 结果
     */
    Integer saveBatchBind(String projectId);


}
