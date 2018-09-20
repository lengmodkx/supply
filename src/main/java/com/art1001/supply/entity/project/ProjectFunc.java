package com.art1001.supply.entity.project;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

/**
 * projectEntity
 */
@Data
@ToString
public class ProjectFunc extends Model<ProjectFunc> {
	
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

	@Override
	protected Serializable pkVal() {
		return this.funcId;
	}
}