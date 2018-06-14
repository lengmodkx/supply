package com.art1001.supply.entity.organization;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * organizationEntity
 */
@Data
public class Organization extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * 企业id
	 */
	private String organizationId;


	/**
	 * 企业名称
	 */
	private String organizationName;


	/**
	 * 企业头像
	 */
	private String organizationImgae;


	/**
	 * 企业简介
	 */
	private String organizationDes;


	/**
	 * 0私有企业1公开企业
	 */
	private Integer isPublic;


	/**
	 * 企业拥有着
	 */
	private String organizationMember;
}