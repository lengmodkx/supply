package com.art1001.supply.entity.collect;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * collectEntity
 */
@Data
public class TaskCollect extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
	private String id;


	/**
	 * 用户id
	 */
	private String memberId;


	/**
	 * 项目id
	 */
	private String projectId;


	/**
	 * 任务id
	 */
	private String taskId;

	/**
	 * 用户头像
	 */
	private String memberImg;


	/**
	 * 用户名
	 */
	private String memberName;

}