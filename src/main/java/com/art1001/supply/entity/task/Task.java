package com.art1001.supply.entity.task;

import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * taskEntity
 */
@Data
@TableName("prm_task")
public class Task extends Model<Task> {
	
	private static final long serialVersionUID = 1L;


	/**
	 * task_id
	 */
	@TableId(value = "task_id",type = IdType.UUID)
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
	@TableField("`repeat`")
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

	@TableField(exist = false)
	private List<Tag> tagList;
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
	 * 执行者名字
	 */
	@TableField(exist = false)
	private String memberName;
	/**
	 * 执行者头像
	 */
	@TableField(exist = false)
	private String memberImg;
	/**
	 * 参与者
	 */
	private String taskUIds;

	/**
	 * 任务类型
	 */
	private String taskType;


	/**
	 * 任务菜单id
	 */
	private String taskMenuId;

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

	/**
	 * 自定义重复时间
	 */
	private Long repetitionTime;

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
	 * 任务的日历日期
	 */
	private Long taskCalendar;

	/**
	 * 该任务所在的项目
	 */
	private Project project;

	/**
	 * 该任务的参与者
	 */
	@TableField(exist = false)
	private List<UserEntity> joinInfo;

	/**
	 * 该任务的子任务
	 */
	@TableField(exist = false)
	private List<Task> taskList;

	/**
	 * 任务附件
	 */
	@TableField(exist = false)
	private List<File> fileList;
	/**
	 * 任务的赞
	 */
	private Integer fabulousCount;

	@Override
	protected Serializable pkVal() {
		return this.taskId;
	}


}