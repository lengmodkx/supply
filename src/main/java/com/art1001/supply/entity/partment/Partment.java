package com.art1001.supply.entity.partment;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * partmentEntity
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName(value = "prm_parment")
public class Partment extends Model<Partment> {
	
	private static final long serialVersionUID = 1L;


	/**
	 * 部门id
	 */
	private String partmentId;


	/**
	 * 组织id
	 */
	private String organizationId;


	/**
	 * 部门logo
	 */
	private String partmentLogo;


	/**
	 * 部门名称
	 */
	private String partmentName;


	/**
	 * 部门排序
	 */
	private Integer partmentOrder;
	/**
	 * 创建时间
	 */
	private Long createTime;
	/**
	 * 修改时间
	 */
	private Long updateTime;

	private Integer orderBy;
	@Override
	protected Serializable pkVal() {
		return this.partmentId;
	}
}