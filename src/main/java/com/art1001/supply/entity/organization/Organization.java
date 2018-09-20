package com.art1001.supply.entity.organization;
import java.io.Serializable;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

/**
 * organizationEntity
 */
@Data
@ToString
public class Organization extends Model<Organization> {
	
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
	private String organizationImage;


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

	/**
	 * 企业联系人
	 */
	private String contact;

	/**
	 * 企业联系人手机号
	 */
	private String contactPhone;

	/**
	 * 创建时间
	 */
	private Long createTime;
	/**
	 * 修改时间
	 */
	private Long updateTime;

	@Override
	protected Serializable pkVal() {
		return this.organizationId;
	}
}