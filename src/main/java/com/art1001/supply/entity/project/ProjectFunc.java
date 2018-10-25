package com.art1001.supply.entity.project;

import lombok.Data;
import lombok.ToString;

/**
 * projectEntity
 */
@Data
@ToString
public class ProjectFunc {
	
	private static final long serialVersionUID = 1L;

	/**
	 * p_name
	 */
	private String funcName;


	/**
	 * is_open
	 */
	private boolean isOpen;

	/**
	 * 后缀
	 */
	private String suffix;

}