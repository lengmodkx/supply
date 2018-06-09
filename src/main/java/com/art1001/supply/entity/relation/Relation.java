package com.art1001.supply.entity.relation;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * relationEntity
 */
@Data
public class Relation extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * relation_id
	 */
	private String relationId;


	/**
	 * 名称
	 */
	private String relationName;


	/**
	 * 父级id
	 */
	private String parentId;


	/**
	 * 项目id
	 */
	private String projectId;


	/**
	 * 创建时间
	 */


	/**
	 * 更新时间
	 */

}