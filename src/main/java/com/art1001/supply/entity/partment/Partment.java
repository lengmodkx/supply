package com.art1001.supply.entity.partment;

import com.art1001.supply.entity.organization.OrganizationMember;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * partmentEntity
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName(value = "prm_partment")
public class Partment extends Model<Partment> {
	
	private static final long serialVersionUID = 1L;


	/**
	 * 部门id
	 */
	@TableId(type = IdType.UUID)
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

	/**
	 *  父级部门的id
	 */
	private String parentId;

	/**
	 * 是否存在子部门
	 */
	@TableField(exist = false)
	private Boolean hasPartment;



	@Override
	protected Serializable pkVal() {
		return this.partmentId;
	}
}