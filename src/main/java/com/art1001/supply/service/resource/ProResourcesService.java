package com.art1001.supply.service.resource;

import com.art1001.supply.entity.resource.ProResources;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 资源表 服务类
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
public interface ProResourcesService extends IService<ProResources> {

    /**
     * 获取到该成员在项目中的资源key集合
     * 两个参数任一为空 则返回null
     * @author heShaoHua
     * @describe 暂无
     * @param projectId 项目id
     * @param memberId 成员id
     * @updateInfo 暂无
     * @date 2019/6/19 17:29
     * @return key集合
     */
    List<String> getMemberResourceKey(String projectId, String memberId);

    /**
     * 根据Rid集合查询出一组资源key
     * @author heShaoHua
     * @describe 暂无
     * @param rIds 资源id集合
     * @updateInfo 暂无
     * @date 2019/6/19 17:35
     * @return 资源key集合
     */
    List<String> getResourceKeyByRIds(List<String> rIds);
}
