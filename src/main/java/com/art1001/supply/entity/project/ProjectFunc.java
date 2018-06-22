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
	private Integer funcId;


	/**
	 * p_name
	 */
	private String funcName;


	/**
	 * p_order
	 */
	private Integer funcOrder;


	/**
	 * project_id
	 */
	private String projectId;


	/**
	 * is_open
	 */
	private Integer isOpen;

}