package com.art1001.supply.entity.organization;

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
@TableName("prm_organization_group")
public class OrganizationGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 群组id
     */
    @TableId(type = IdType.UUID)
    private String groupId;

    /**
     * 群组名称
     */
    private String groupName;

    /**
     * 该群组属于哪个组织
     */
    private String organizationId;

    /**
     * 拥有者
     */
    private String owner;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;


    private String consulGroup;

    @TableField(exist = false)
    private Object consulGroupObject;

}
