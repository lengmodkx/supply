package com.art1001.supply.entity.template;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * Entity
 */
@Data
public class Template extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * template_id
	 */
	private String templateId;


	/**
	 * template_cover
	 */
	private String templateCover;


	/**
	 * template_name
	 */
	private String templateName;


	/**
	 * template_des
	 */
	private String templateDes;


	/**
	 * template_title
	 */
	private String templateTitle;

}