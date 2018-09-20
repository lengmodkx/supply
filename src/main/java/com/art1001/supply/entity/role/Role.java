package com.art1001.supply.entity.role;

import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
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
public class Role extends Model<Role> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@TableId(value = "r_id",type = IdType.AUTO)
	private Integer id;

	/*
	 * 角色名
	 */
	private String name;
	/*
	 * 角色key
	 */
	private String key;
	/*
	 * 角色状态
	 */
	private Integer status;
	/*
	 * 角色描述信息
	 */
	private String description;
	/*
	 * 角色创建时间
	 */
	private Date createTime;
	/*
	 * 角色更新时间
	 */
	private Date updateTime;
	/*
	 * 角色下所有用户列表结合
	 */
	private List<UserEntity> userList;
	/**
	 * 企业id
	 */
	private String organizationId;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}