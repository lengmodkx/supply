package com.art1001.supply.application.assembler;

import com.art1001.supply.entity.relation.GroupUser;
import com.art1001.supply.util.IdGen;
import com.art1001.supply.util.ValidatedUtil;
import org.springframework.stereotype.Component;

/**
 * @author heshaohua
 * @date 2019/10/9 11:11
 **/
@Component
public class GroupUserAssembler {

    public GroupUser transFormGroupUser(String groupId, String userId){
        ValidatedUtil.filterNullParam(groupId, userId);

        GroupUser groupUser = new GroupUser();
        groupUser.setGroupId(groupId);
        groupUser.setUserId(userId);

        return groupUser;
    }
}
