package com.art1001.supply.service.resource;

import com.art1001.supply.entity.resource.ProResourceRoleBindTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
public interface ProResourceRoleBindTemplateService extends IService<ProResourceRoleBindTemplate> {

    /**
     * 根据项目角色的key 获取到对应key的角色资源关系模板
     * @author heShaoHua
     * @describe 暂无
     * @updateInfo 暂无
     * @date 2019/6/20 14:50
     * @return roleKey为key 模板数据为value
     */
    Map<String,String> getRoleResourcesTemplateByRoleKey();
}
