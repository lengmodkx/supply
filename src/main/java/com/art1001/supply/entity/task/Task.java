package com.art1001.supply.entity.task;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;

import com.art1001.supply.entity.project.Project;
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
	 * 任务菜单id
	 */
	private String taskMenuId;


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

	/**
	 * 自定义重复时间
	 */
	private Long repetitionTime;

	/**
	 * 任务得赞数
	 */
	private Integer fabulousCount;

	/**
	 * 任务的隐私模式
	 */
	private Integer privacyPattern;

	/**
	 * 其他
	 */
	private String other;

	/**
	 * 任务的排序编号
	 */
	private Integer order;

	/**
	 * 任务成员关系的信息
	 */
	private TaskMember taskMember;

	/**
	 * 任务的日历日期
	 */
	private Long taskCalendar;

	/**
	 * 该任务所在的项目
	 */
	private Project project;

}