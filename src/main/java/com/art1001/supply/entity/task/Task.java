package com.art1001.supply.entity.task;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * taskEntity
 */
@Data
public class Task extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * task_id
	 */
	private String taskId;


	/**
	 * 项目id
	 */
	private String projectId;


	/**
	 * 任务名称
	 */
	private String taskName;


	/**
	 * 开始时间
	 */
	private Long startTime;

	/**
	 * 结束时间
	 */
	private Long endTime;


	/**
	 * 设置任务重复
	 */
	private String repeat;


	/**
	 * 任务提醒
	 */
	private String remind;


	/**
	 * 备注
	 */
	private String remarks;


	/**
	 * 优先级
	 */
	private String priority;


	/**
	 * 多个tag_id
	 */
	private String tagId;


	/**
	 * 任务的层级
	 */
	private Integer level;


	/**
	 * 父级id
	 */
	private String parentId;


	/**
	 * 创建者
	 */
	private String memberId;


	/**
	 * 执行者
	 */
	private String executor;


	/**
	 * 任务类型
	 */
	private String taskType;


	/**
	 * 任务分组id
	 */
	private String taskGroupId;


	/**
	 * 任务状态
	 */
	private String taskStatus;


	/**
	 * 任务码
	 */
	private String taskCode;


	/**
	 * 是否删除
	 */
	private Integer taskDel;


	/**
	 * 创建时间
	 */

	private Long createTime;

	/**
	 * 更新时间
	 */
	private Long updateTime;

	/**
	 * 提醒时间
	 */
	private Long remindTime;

}