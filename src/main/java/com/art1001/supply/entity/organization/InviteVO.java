package com.art1001.supply.entity.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName InviteVO
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/5/18 15:24
 * @Discription 邀请成员VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InviteVO {
    /**
     * 企业当前成员数量
     */
    private Integer memberNum;

    /**
     * 到期时间
     */
    private String effectiveDate;
    /**
     * 拼接地址
     */
    private String printUrl;
}
