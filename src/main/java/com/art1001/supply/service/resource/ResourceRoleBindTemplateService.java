package com.art1001.supply.service.resource;

import com.art1001.supply.entity.resource.ResourceRoleBindTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author heshaohua
 * @since 2019-05-29
 */
public interface ResourceRoleBindTemplateService extends IService<ResourceRoleBindTemplate> {

    /**
     * 根据角色key获取到这个角色应有的资源id (根据模板获取)
     * @author heShaoHua
     * @describe 暂无
     * @param roleKey 角色keu
     * @updateInfo 暂无
     * @date 2019/5/29 16:29
     * @return 资源id集合
     */
    List<String> getRoleBindResourceIds(String roleKey);

}
