package com.art1001.supply.entity.user;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

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
@TableName("tb_user")
public class UserEntity extends Model<UserEntity> {

	private static final long serialVersionUID = -6743567631108323096L;

	public String id;

	/*
	 * 用户真实姓名
	 */
	private String userName;
	/*
	 * 这里账户名称统一使用邮箱/手机号
	 */
	private String accountName;

	/**
	 * 用户地址
	 */
	private String address;

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
	 * 前端列表页使用
	 */
	private String roleName;

	//企业id
	private String organizationId;
	//部门id
	private String partmentId;
	//企业员工账号是否被停用，0不是，1是
	private Integer organizationLable;

	/**
	 * 用户头像
	 */
	private String image;

	/**
	 * 用户默认头像
	 */
	private String defaultImage;

	/**
	 * 用户职位
	 */
	private String job;

	/**
	 * 用户性别
	 */
	private int sex;

	/**
	 * 用户出生日期
	 */
	private Date birthday;

	/**
	 * 用户手机号
	 */
	private String telephone;

	/**
	 * 用户邮箱
	 */
	private String email;

	public UserEntity() {

	}

	@Override
	protected Serializable pkVal() {
		return this.id;
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
		this.roleName = userEntity.getRoleName();
		this.organizationId = userEntity.getOrganizationId();
		this.partmentId = userEntity.getPartmentId();
	}
}
