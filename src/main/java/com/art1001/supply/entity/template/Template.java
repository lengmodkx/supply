package com.art1001.supply.entity.template;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("template")
public class Template extends Model<Template> {
	
	private static final long serialVersionUID = 1L;


	/**
	 * template_id
	 */
	@TableId(type = IdType.UUID)
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

	private String orgId;

	@Override
	protected Serializable pkVal() {
		return this.templateId;
	}
}