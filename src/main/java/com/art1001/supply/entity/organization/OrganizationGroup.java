package com.art1001.supply.entity.organization;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 组织群组Entity
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName(value = "prm_organization_group")
public class OrganizationGroup extends Model<OrganizationGroup> {
	
	private static final long serialVersionUID = 1L;


	/**
	 * 群组id
	 */
	private String groupId;


	/**
	 * 群组名称
	 */
	private String groupName;


	/**
	 * 该群组属于哪个组织
	 */
	private String organizationId;


	/**
	 * 拥有者
	 */
	private String owner;


	/**
	 * 成员id
	 */
	private String memberIds;

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
		return this.groupId;
	}
}