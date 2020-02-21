package com.art1001.supply.entity.user;

import lombok.Data;

/**
 * @author shaohua
 * @date 2020/2/21 20:59
 */
@Data
public class ProjectMemberInfo {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户头像
     */
    private String memberImg;

    /**
     * 用户姓名
     */
    private String memberName;

    /**
     * 用户手机号码
     */
    private String accountName;

    /**
     * 用户职位
     */
    private String job;
}
