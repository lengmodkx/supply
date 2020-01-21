package com.art1001.supply.entity.role;

import com.art1001.supply.validation.role.AddProRoleValidation;
import com.art1001.supply.validation.role.RoleIdValidation;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
@Data
@TableName("sys_pro_role")
public class ProRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色id
     */
    @TableId(value = "role_id", type = IdType.AUTO)
    private Integer roleId;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空!",groups = AddProRoleValidation.class)
    private String roleName;

    /**
     * 角色key
     */
    @NotBlank(message = "角色key不能为空!",groups = AddProRoleValidation.class)
    private String roleKey;

    /**
     * 角色状态,0：正常；1：删除
     */
    private Integer roleStatus;

    /**
     * 角色描述
     */
    @NotBlank(message = "角色描述不能为空!",groups = AddProRoleValidation.class)
    private String roleDes;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 项目id
     */
    @NotBlank(message = "项目id不能为空!",groups = AddProRoleValidation.class)
    private String publicId;

    /**
     * 是否是该企业的默认角色 0:不是 1:是
     */
    private Boolean isDefault;

    @TableField(exist = false)
    private Boolean _disabled;

    /**
     * 是否是系统初始化的角色 0:不是 1:是
     */
    private Boolean isSystemInit;

    @TableField(exist = false)
    private Boolean currentCheck;


}
