package com.art1001.supply.entity.template;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 模板Entity
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName("template_data")
public class TemplateData extends Model<TemplateData> {
	
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

	private int menuOrder;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}