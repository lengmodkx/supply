package com.art1001.supply.entity.binding;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

/**
 * Entity
 */
@Data
@ToString
@TableName(value = "prm_binding")
public class Binding extends Model<Binding> {
	
	private static final long serialVersionUID = 1L;


	/**
	 * 关联关系id
	 */
	@TableId(value = "id",type = IdType.UUID)
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
	private JSONObject bindContent;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}