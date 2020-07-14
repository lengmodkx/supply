package com.art1001.supply.entity.organization;

import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author DindDangMao
 * @since 2019-04-29
 */
@Data
@TableName("prm_organization_group_member")
public class OrganizationGroupMember implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.UUID)
    private String id;

    /**
     * 分组id
     */
    private String groupId;

    /**
     * 成员id
     */
    private String memberId;

    /**
     * 手机号
     */
    @TableField(exist = false)
    private String phone;

    /**
     * 邮箱
     */
    @TableField(exist = false)
    private String memberEmail;
    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 成员头像
     */
    @TableField(exist = false)
    private String image;

    /**
     * 成员的名称
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 成员id
     */
    @TableField(exist = false)
    private String userId;

    /**
     * 是否是群组的拥有者
     */
    @TableField(exist = false)
    private Boolean isOwner;

    @TableField(exist = false)
    private UserEntity userEntity;


}
