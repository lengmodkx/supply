package com.art1001.supply.entity.organization;

import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author 汪亚锋
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName(value = "prm_organization_member")
public class OrganizationMember extends Model<OrganizationMember> {
	
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
	@TableId(value = "id",type = IdType.UUID)
	private String id;


	/**
	 * 企业id
	 */
	private String organizationId;


	/**
	 * 部门id
	 */
	private String partmentId;


	/**
	 * 会员id
	 */
	private String memberId;


	/**
	 * 是否是企业拥有着，0是成员 1是拥有着
	 */
	private Integer organizationLable;


	/**
	 * 是否是企业拥有着，0是成员 1是拥有着
	 */
	private Integer partmentLable;

	/**
	 * 企业用户是否被停用 0停用，1启用
	 */
	private Integer memberLock;

	/**
	 * 用户实体
	 */
	@TableField(exist = false)
	private UserEntity userEntity;

	/**
	 * 创建时间
	 */
	private Long createTime;
	/**
	 * 修改时间
	 */
	private Long updateTime;

	/**
	 * 是否是当前用户所在的企业
	 */
	private Boolean userDefault;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}