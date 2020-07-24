package com.art1001.supply.entity.partment;

import com.art1001.supply.entity.organization.OrganizationMember;
import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author DindDangMao
 * @since 2019-04-29
 */
@Data
@TableName("prm_partment_member")
public class PartmentMember implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(type = IdType.UUID)
    private String id;

    /**
     * 部门id
     */
    private String partmentId;

    /**
     * 部门名称
     */
    @TableField(exist = false)
    private String partmentName;

    /**
     * 部门logo
     */
    @TableField(exist = false)
    private String partmentLogo;

    /**
     * 用户id
     */
    private String memberId;

    /**
     * 是否是负责人  0:否   1:是 
     */
    private Boolean isMaster;

    /**
     * 成员身份 1:成员 2:拥有者 3:管理员
     */
    private Integer memberLabel;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 最后更新时间
     */
    private Long updateTime;

    @TableField(exist = false)
    private String parentId;

    /**
     * 企业名称
     */
    @TableField(exist = false)
    private String organizationName;
    /**
     * 成员的基本信息
     */
    @TableField(exist = false)
    private UserEntity userEntity;

    @TableField(value = "member_type")
    private String memberType;

    @TableField(exist = false)
    private OrganizationMember organizationMember;

    @TableField(exist = false)
    private List<OrganizationMember> organizationMembers;

}
