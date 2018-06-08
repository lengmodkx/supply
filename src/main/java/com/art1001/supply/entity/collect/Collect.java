package com.art1001.supply.entity.collect;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * collectEntity
 */
@Data
public class Collect extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
	private String id;


	/**
	 * 用户id
	 */
	private String userId;


	/**
	 * 项目id
	 */
	private String projectId;


	/**
	 * create_time
	 */


	/**
	 * update_time
	 */

}