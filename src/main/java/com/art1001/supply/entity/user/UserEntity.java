package com.art1001.supply.entity.user;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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

	@TableId(value = "u_id",type = IdType.UUID)
	public String id;

	/*
	 * 用户真实姓名
	 */
	@TableField(value = "u_name")
	private String userName;
	/*
	 * 这里账户名称统一使用邮箱/手机号
	 */
	@TableField(value = "u_account_name")
	private String accountName;

	/**
	 * 用户地址
	 */
	@TableField(value = "u_address")
	private String address;

	/*
	 * 密码
	 */
	@TableField(value = "u_password")
	private String password;
	/*
	 * 逻辑删除状态：0：正常；1：删除
	 */
	@TableField(value = "u_delete_status")
	private Integer deleteStatus;
	/*
	 * 是否锁定：0：正常；1：锁定
	 */
	@TableField(value = "u_locked")
	private Integer locked;
	/*
	 * 描述
	 */
	@TableField(value = "u_description")
	private String description;
	/*
	 * 加密盐
	 */
	@TableField(value = "u_credentials_salt")
	private String credentialsSalt;
	/*
	 * 这里使用accountName
	 */
	@TableField(value = "u_creator_name")
	private String creatorName;
	/*
	 * 创建时间
	 */
	@TableField(value = "u_create_time")
	private Date createTime;
	/*
	 * 更新时间
	 */
	@TableField(value = "u_update_time")
	private Date updateTime;

	/**
	 * 用户头像
	 */
	@TableField(value = "u_image")
	private String image;

	/**
	 * 用户默认头像
	 */
	@TableField(value = "u_default_img")
	private String defaultImage;

	/**
	 * 用户职位
	 */
	@TableField(value = "u_job")
	private String job;

	/**
	 * 用户性别
	 */
	@TableField(value = "u_sex")
	private int sex;

	/**
	 * 用户出生日期
	 */
	@TableField(value = "u_birthday")
	private Date birthday;

	/**
	 * 用户手机号
	 */
	@TableField(value = "u_telephone")
	private String telephone;

	/**
	 * 用户邮箱
	 */
	@TableField(value = "u_email")
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
	}
}
