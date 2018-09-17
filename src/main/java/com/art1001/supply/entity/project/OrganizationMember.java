package com.art1001.supply.entity.project;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;

import com.art1001.supply.entity.user.UserEntity;
import lombok.Data;

/**
 * projectEntity
 */
@Data
public class OrganizationMember extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
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

	private UserEntity userEntity;
}