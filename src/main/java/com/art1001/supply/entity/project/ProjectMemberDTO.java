package com.art1001.supply.entity.project;

import com.art1001.supply.entity.organization.OrganizationMemberInfo;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName ProjectMemberDTO
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/4/21 17:07
 * @Discription 企业详细信息DTO
 */

@Data
public class ProjectMemberDTO {

    /**
         * 头像
     */
    @TableField(exist = false)
    private String memberImg;


    @TableField(exist = false)
    private Project project;

    /**
     * 用户名
     */
    @TableField(exist = false)
    private String memberName;

    /**
     * 成员用户名
     */
    @TableField(exist = false)
    private String accountName;

    /**
     * 成员职位
     */
    @TableField(exist = false)
    private String job;

    /**
     * 手机号
     */
    @TableField(exist = false)
    private String memberPhone;

    /**
     * 成员身份
     * 0:普通成员 1:拥有者
     */
    @TableField("member_label")
    private Integer memberLabel;


    @TableField(exist = false)
    private Integer lable;

    /**
     * 是否接受项目的消息
     */
    @TableField("is_receiveMessage")
    private Integer isReceiveMessage;

    /**
     * 是否收藏该项目
     */
    @TableField("collect")
    private Integer collect;

    /**
     * 项目成员的角色,默认成员
     */
    @TableField("role_id")
    private Integer roleId;

    /**
         * 角色key
     */
    private String roleKey;

    @TableField(exist=false)
    private String defaultImage;


    private Boolean visible = false;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long updateTime;

    /**
     * 用户在该项目中的默认分组
     * @return
     */
    private String defaultGroup;

    /**
     * 默认视图
     * 0:列表 1:看板 2:时间
     */
    private int defaultView;

    /**
     * 是否是用当前所在项目
     */
    private Boolean current;

    /**
         * 企业成员详细信息
     */
    private OrganizationMemberInfo organizationMemberInfo;
}
