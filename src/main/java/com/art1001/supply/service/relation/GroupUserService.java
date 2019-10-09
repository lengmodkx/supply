package com.art1001.supply.service.relation;

import com.art1001.supply.entity.relation.GroupUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author heshaohua
 * @since 2019-10-09
 */
public interface GroupUserService extends IService<GroupUser> {

    /**
     * 添加 用户和分组的关联关系
     * @author heShaoHua
     * @describe 暂无
     * @param groupId 分组id
     * @param userId 用户id
     * @updateInfo 暂无
     * @date 2019/10/9 11:03
     */
    void additionGroupUser(String groupId, String userId);

    /**
     * 检查分组用户关系是否已经存在
     * @author heShaoHua
     * @describe 暂无
     * @param groupId 分组id
     * @param userId 用户id
     * @updateInfo 暂无
     * @date 2019/10/9 11:05
     * @return 存在的记录条数
     */
    Integer checkGroupUserIsExist(String groupId, String userId);

    /**
     * 检查用户所属项目 和 分组所属项目 是否一致
     * @author heShaoHua
     * @describe 暂无
     * @param groupId 分组id
     * @param userId 用户id
     * @updateInfo 暂无
     * @date 2019/10/9 11:23
     * @return 是否一致
     */
    Boolean checkGroupAndUserProjectIsIdentical(String groupId, String userId);

}
