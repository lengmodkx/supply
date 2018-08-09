package com.art1001.supply.entity.template;

import java.io.Serializable;
import lombok.Data;

/**
 * 模板Entity
 */
@Data
public class TemplateData implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
	private String id;


	/**
	 * template_id
	 */
	private String templateId;


	/**
	 * menu_name
	 */
	private String menuName;


	/**
	 * task_name
	 */
	private String taskName;


	/**
	 * remarks
	 */
	private String remarks;


	/**
	 * parent_id
	 */
	private String parentId;

}