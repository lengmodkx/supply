package com.art1001.supply.entity.role;

import com.art1001.supply.entity.base.BaseEntity;
import com.art1001.supply.entity.user.UserEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.Alias;

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
@Accessors(chain = true)
@Alias("roleEntity")
public class RoleEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public Long id;

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

	@Override
	public String toString() {
		return "RoleEntity [id=" + id + ", name=" + name + ", key=" + key + ", status="
				+ status + ", description=" + description + ", createTime="
				+ createTime + ", updateTime=" + updateTime + ", userList="
				+ userList + "]";
	}

}