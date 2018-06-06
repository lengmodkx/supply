package com.art1001.supply.entity.task;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * taskFileEntity
 */
@Data
public class TaskFile extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
	private String id;


	/**
	 * 绑定id
	 */
	private String bindingId;


	/**
	 * 其他id
	 */
	private String otherId;


	/**
	 * 关联的类型
	 */
	private String otherType;


	/**
	 * 创建时间
	 */


	/**
	 * 更新时间
	 */

}