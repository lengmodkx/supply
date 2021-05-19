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
@TableName(value = "template_binding")
public class TemplateBinding extends Model<TemplateBinding> {
	
	/**
	 * 关联关系id
	 */
	@TableId(value = "id",type = IdType.ASSIGN_UUID)
	private String id;


	/**
	 * 任务，日程，文件，分享id
	 */
	private String publicId;


	/**
	 * 任务，日程，文件，分享id
	 */
	private String bindId;


	/**
	 * 任务，日程，文件，分享
	 */
	private String publicType;

	/**
	 * 绑定的内容
	 */
	private String bindContent;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}