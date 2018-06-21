package com.art1001.supply.entity.project;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * projectEntity
 */
@Data
public class ProjectFunc extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * p_id
	 */
	private Integer pId;


	/**
	 * p_name
	 */
	private String pName;


	/**
	 * p_order
	 */
	private Integer pOrder;


	/**
	 * project_id
	 */
	private String projectId;


	/**
	 * is_open
	 */
	private Integer isOpen;

}