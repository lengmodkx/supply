package com.art1001.supply.entity.template;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * Entity
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class Template extends Model<Template> {
	
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

	@Override
	protected Serializable pkVal() {
		return this.templateId;
	}
}