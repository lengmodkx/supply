package com.art1001.supply.entity.partment;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * partmentEntity
 */
@Data
public class Partment extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * 部门id
	 */
	private String partmentId;


	/**
	 * 组织id
	 */
	private String organizationId;


	/**
	 * 部门logo
	 */
	private String partmentLogo;


	/**
	 * 部门名称
	 */
	private String partmentName;


	/**
	 * 部门排序
	 */
	private Integer partmentOrder;
}