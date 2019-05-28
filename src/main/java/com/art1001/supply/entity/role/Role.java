package com.art1001.supply.entity.role;

import com.art1001.supply.entity.resource.ResourceEntity;
import com.art1001.supply.validation.role.RoleIdValidation;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 *
 * @ClassName: RoleEntity
 * @Description: 角色信息
 * @author wangyafeng
 * @date 2016年7月12日 下午2:39:54
 *
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName(value = "sys_role")
public class Role extends Model<Role> {


	private static final long serialVersionUID = 1L;
	/**
	 * 角色id
	 */
	@NotNull(message = "值不能为空!",groups = RoleIdValidation.class)
	@Digits(integer = 1,fraction = 20,message = "值必须是数组类型!",groups = RoleIdValidation.class)
	@TableId(value = "role_id",type = IdType.AUTO)
	private Integer roleId;

	/*
	 * 角色名
	 */
	private String roleName;

	/*
	 * 角色key
	 */
	private String roleKey;
	/*
	 * 角色状态
	 */
	private Integer roleStatus;
	/*
	 * 角色描述信息
	 */
	private String roleDes;
	/*
	 * 角色创建时间
	 */
	private Timestamp createTime;
	/*
	 * 角色更新时间
	 */
	private Timestamp updateTime;
	/**
	 * 企业id
	 */
	private String organizationId;

	@TableField(exist = false)
	private List<ResourceEntity> resources;

	/**
	 * 是否是该企业的默认角色
	 */
	private Boolean isDefault;

	/**
	 * 是否是系统默认的初始化角色
	 */
	private Boolean isSystemInit;

	@Override
	protected Serializable pkVal() {
		return this.roleId;
	}
}