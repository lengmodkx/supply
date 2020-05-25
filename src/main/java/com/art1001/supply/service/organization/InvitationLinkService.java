package com.art1001.supply.service.organization;

import com.art1001.supply.entity.organization.InvitationLink;
import com.art1001.supply.entity.organization.InvitationLinkVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface InvitationLinkService extends IService<InvitationLink> {

    /**
     * 生成邀请链接
     * @param orgId
     * @return
     */
    InvitationLinkVO getOrganizationMemberByUrl(String orgId);

    /**
     * 获取邀请链接的详细信息
     * @param hash
     * @return
     */
    InvitationLink getRedrectUrl(String hash);
}
