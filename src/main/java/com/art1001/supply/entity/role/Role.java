package com.art1001.supply.entity.role;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 * @ClassName: RoleEntity
 * @Description: 角色信息
 * @author wangyafeng
 * @date 2016年7月12日 下午2:39:54
 *
 */
@Data
@TableName(value = "tb_role")
public class Role extends Model<Role> {


	private static final long serialVersionUID = 1L;
	/**
	 * 角色id
	 */
	@TableId(value = "role_id",type = IdType.AUTO)
	private Integer roleId;

	/*
	 * 角色名
	 */
	@TableField("role_name")
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

	@Override
	protected Serializable pkVal() {
		return this.roleId;
	}
}