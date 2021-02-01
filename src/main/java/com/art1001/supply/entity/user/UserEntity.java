package com.art1001.supply.entity.user;
import com.art1001.supply.entity.partment.Partment;
import com.art1001.supply.entity.role.Role;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/***
 * 
 * @ClassName: UserEntity
 * @Description: 用户账户信息
 * @author wangyafeng
 * @date 2016年7月12日 下午2:39:12
 *
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName("tb_user")

public class UserEntity extends Model<UserEntity> {

	private static final long serialVersionUID = -6743567631108323096L;

	@TableId(value = "user_id",type = IdType.UUID)
	public String userId;

	/**
	 * 用户真实姓名
	 */
	private String userName;

	/**
	 * 这里账户名称统一使用邮箱/手机号
	 */
	private String accountName;

	/**
	 * 昵称（唯一不可重复）
 	 */
	private String nickName;
	/***
	 * 用户地址
	 */
	private String address;

	/**
	 * 密码
	 */
	@JsonIgnore
	private String password;
	/**
	 * 逻辑删除状态：0：正常；1：删除
	 */
	@JsonIgnore
	private Integer deleteStatus;
	/**
	 * 是否锁定：0：正常；1：锁定
	 */
	@JsonIgnore
	private Integer locked;
	/**
	 * 描述
	 */
	@JsonIgnore
	private String description;
	/**
	 * 加密盐
	 */
	@JsonIgnore
	private String credentialsSalt;
	/**
	 * 这里使用accountName
	 */
	@JsonIgnore
	private String creatorName;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

	/***
	 * 用户头像
	 */
	private String image;

	/***
	 * 用户默认头像
	 */
	private String defaultImage;

	/***
	 * 用户职位
	 */
	private String job;

	/**
	 * 部门名称
	 */
	@TableField(exist = false)
	private String partmentName;

	/**
	 * 角色名
	 */
	@TableField(exist = false)
	private Integer role;

	/**
	 * 用户性别
	 */
	private Integer sex;

	@TableField(exist = false)
	private Partment partment;

	/***
	 * 用户出生日期
	 */
	@JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
	private Date birthday;

	/***
	 * 用户手机号
	 */
	private String telephone;

	/***
	 * 用户邮箱
	 */
	private String email;

	/**
	 * 是否管理员 0否1是
	 */
	private Integer isAdmin;

	/**
	 * 签名
	 */
	private String signature;

	/***
	 * 用户vip类型
	 * 0.普通会员
	 * 1.专业版会员
	 * 2.建筑版会员
	 * 3.装饰板会员
	 * 4.mep版本会员
	 */
	private Integer vip;

  //企业中是否已添加角色为成员，null或0为未添加，1为添加
	@TableField(exist = false)
	private Integer existId;

	@JsonIgnore
	private String sessionKey;
	@JsonIgnore
	private String wxAppOpenId;
	@JsonIgnore
	private String wxUnionId;
	@JsonIgnore
	private String wxOpenId;

	@TableField(exist = false)
	private Boolean visible = false;

	/**
	 * 是否关注 0否 1是
	 */
	@TableField(exist = false)
	private Integer isAttention;
	/***
	 * 用户默认所在的企业
	 */
	@TableField(exist = false)
	private String defaultOrgId;
	@JsonIgnore
	@TableField(exist = false)
	private List<Role> roles;

	public UserEntity() {

	}

	@Override
	protected Serializable pkVal() {
		return this.userId;
	}

	public UserEntity(UserEntity userEntity) {
		this.userId = userEntity.getUserId();
		this.accountName = userEntity.getAccountName();
		this.password = userEntity.getPassword();
		this.deleteStatus = userEntity.getDeleteStatus();
		this.locked = userEntity.getLocked();
		this.description = userEntity.getDescription();
		this.credentialsSalt = userEntity.getCredentialsSalt();
		this.creatorName = userEntity.getCreatorName();
		this.createTime = userEntity.getCreateTime();
		this.updateTime = userEntity.getUpdateTime();
		this.existId = userEntity.getExistId();
	}
}
