package com.art1001.supply.entity.task;

import java.io.Serializable;

import com.art1001.supply.entity.base.BaseEntity;
import lombok.Data;

/**
 * 111Entity
 */
@Data
public class TaskLog extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
	private String id;


	/**
	 * 任务id
	 */
	private String taskId;


	/**
	 * 用户id
	 */
	private String memberId;


	/**
	 * 用户名
	 */
	private String memberName;


	/**
	 * 头像
	 */
	private String memberImg;


	/**
	 * 内容
	 */
	private String content;

	/**
	 * 日志的类型  0: 日志  1:聊天内容
	 */
	private Integer logType;

	/**
	 * 创建时间
	 */
	private Long createTime;
}