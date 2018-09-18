package com.art1001.supply.entity.organization;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * 组织群组Entity
 */
@Data
public class OrganizationGroup extends BaseEntity implements Serializable {
	
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


	/**
	 * 更新时间
	 */

}