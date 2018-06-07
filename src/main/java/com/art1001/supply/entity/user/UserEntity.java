package com.art1001.supply.entity.user;

import com.art1001.supply.entity.role.RoleEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @ClassName: UserEntity
 * @Description: 用户账户信息
 * @author wangyafeng
 * @date 2016年7月12日 下午2:39:12
 *
 */
@Data
@Accessors(chain = true)
@Alias("userEntity")
public class UserEntity implements Serializable {

	public Long id;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * 用户真实姓名
	 */
	private String userName;
	/*
	 * 这里账户名称统一使用邮箱/手机号
	 */
	private String accountName;
	/*
	 * 密码
	 */
	private String password;
	/*
	 * 逻辑删除状态：0：正常；1：删除
	 */
	private Integer deleteStatus;
	/*
	 * 是否锁定：0：正常；1：锁定
	 */
	private Integer locked;
	/*
	 * 描述
	 */
	private String description;
	/*
	 * 加密盐
	 */
	private String credentialsSalt;
	/*
	 * 这里使用accountName
	 */
	private String creatorName;
	/*
	 * 创建时间
	 */
	private Date createTime;
	/*
	 * 更新时间
	 */
	private Date updateTime;
	/*
	 * 所属角色
	 */
	private RoleEntity role;
	/*
	 * 个人资料信息
	 */
	private UserInfoEntity userInfo = new UserInfoEntity();
	/*
	 * 前端列表页使用
	 */
	private String roleName;

	public UserEntity() {

	}

	public UserEntity(UserEntity userEntity) {
		this.id = userEntity.getId();
		this.accountName = userEntity.getAccountName();
		this.password = userEntity.getPassword();
		this.deleteStatus = userEntity.getDeleteStatus();
		this.locked = userEntity.getLocked();
		this.description = userEntity.getDescription();
		this.credentialsSalt = userEntity.getCredentialsSalt();
		this.creatorName = userEntity.getCreatorName();
		this.createTime = userEntity.getCreateTime();
		this.updateTime = userEntity.getUpdateTime();
		this.role = userEntity.getRole();
		this.userInfo = userEntity.getUserInfo();
		this.roleName = userEntity.getRoleName();
	}


	public void setRole(RoleEntity role) {
		this.role = role;
		// 设置角色名称,dtgrid使用
		this.roleName = role.getName();
	}


}
