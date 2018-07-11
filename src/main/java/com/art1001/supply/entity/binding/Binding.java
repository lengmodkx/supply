package com.art1001.supply.entity.binding;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * bindingEntity
 */
@Data
public class Binding extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * 关联关系id
	 */
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
	 * public_type
	 */
	private String publicType;


	/**
	 * 创建时间
	 */


	/**
	 * 更新时间
	 */

}