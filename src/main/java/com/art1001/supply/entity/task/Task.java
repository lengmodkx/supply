package com.art1001.supply.entity.task;

import com.alibaba.fastjson.annotation.JSONField;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.file.FileApiBean;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.schedule.ScheduleApiBean;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.share.ShareApiBean;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * taskEntity
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
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
	 * 分组名称
	 */
	@TableField(exist = false)
	private String groupName;

	@TableField(exist = false)
	private String menuName;
	/**
	 * 开始时间
	 */
	private Long startTime;

	/**
	 * 结束时间
	 */
	private Long endTime;

	/**
	 * 完成的子任务数
	 */
	@TableField(exist = false)
	private int completeCount;

	@TableField(exist = false)
	private boolean isComplete;

	/**
	 * 子任务总数
	 */
	@TableField(exist = false)
	private int childCount;


	/**
	 * 设置任务重复
	 */
	@TableField("`repeat`")
	private String repeat;

	/**
	 * 备注
	 */
	private String remarks;

	/**
	 * 优先级
	 */
	private String priority;

	/**
	 * 标签
	 */
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
	private String executorName;

	/**
	 * 执行者头像
	 */
	@TableField(exist = false)
	private String executorImg;

	/**
	 * 参与者
	 */
	@TableField("task_uids")
	private String taskUIds;

	/**
	 * 任务类型 (保留字段)
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
	private Boolean taskStatus;

	/**
	 * 任务码(保留字段)
	 */
	private String taskCode;

	/**
	 * 是否删除
	 */
	private Integer taskDel;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private Long createTime;

	/**
	 * 更新时间
	 */
	@TableField(fill = FieldFill.UPDATE)
	private Long updateTime;

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
	@TableField("`order`")
	private Integer order;

	/**
	 * 该任务所在的项目
	 */
	@TableField(exist = false)
	private Project project;

	/**
	 * 该任务的参与者
	 */
	@TableField(exist = false)
	private List<UserEntity> joinInfo;

	/**
	 * 该任务的父级任务
	 */
	@TableField(exist = false)
	private Task parentTask;

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
	@TableField(exist = false)
	private Integer fabulousCount;

	/**
	 * 判断是否点赞
	 */
	@TableField(exist = false)
	private Boolean isFabulous;

	/**
	 * 判断是否收藏
	 */
	@TableField(exist = false)
	private Boolean isCollect;

	/**
	 * 任务日志信息
	 */
	@TableField(exist = false)
	private List<Log> logs;

	/**
	 * 关联的任务信息
	 */
	@TableField(exist = false)
	private List<TaskApiBean> bindTasks;

	/**
	 * 关联的文件信息
	 */
	@TableField(exist = false)
	private List<FileApiBean> bindFiles;

	/**
	 * 关联的日程信息
	 */
	@TableField(exist = false)
	private List<ScheduleApiBean> bindSchedules;

	/**
	 * 关联的分享信息
	 */
	@TableField(exist = false)
	private List<ShareApiBean> bindShares;

	/**
	 * 未读消息数
	 */
	@TableField(exist = false)
	private int unReadMsg;

	/**
	 * 是否存在子任务
	 */
	@TableField(exist = false)
	private Boolean isExistSub;

	/**
	 * 任务的进度
	 */
	private Integer progress;

	/**
	 * 关联信息的id
	 */
	@TableField(exist = false)
	private String bindId;


	@Override
	protected Serializable pkVal() {
		return this.taskId;
	}


}