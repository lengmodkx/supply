/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author 邓凯欣 dengkaixin@art1001.com
 * @create 2020/6/17
 * @since 1.0.0
 */
package com.art1001.supply.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户头像
     */
    private String image;
    /**
     * 职位
     */
    private String job;
    /**
     * 部门
     */
    private String partmemt;
    /**
     * 电话号
     */
    private String phone;
    /**
     * 企业id
     */
    private String organizationId;
    /**
     * 企业名
     */
    private String organizationName;
    /**
     * 项目id
     */
    private String projectId;
    /**
     * 项目名
     */
    private String projectName;
    /**
     * 分组id
     */
    private String groupId;

}
