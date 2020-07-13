package com.art1001.supply.entity.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName InvitationLinkVO
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/5/19 13:52
 * @Discription 邀请表VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvitationLinkVO {
    /**
     * 当前企业成员数量
     */
    private Integer memberNum;
    /**
     * 到期时间
     */
    private String expireTime;
    /**
     * 短链接
     */
    private String shortUrl;

    /**
     * hash
     */
    private String hash;

    private String orgId;

    private String projectId;

    private String memberId;
}
