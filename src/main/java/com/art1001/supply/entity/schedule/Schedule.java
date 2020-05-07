package com.art1001.supply.entity.schedule;

import com.art1001.supply.entity.binding.Binding;
import com.art1001.supply.entity.file.FileApiBean;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.share.ShareApiBean;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.TaskApiBean;
import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * scheduleEntity
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName("prm_schedule")
public class Schedule extends Model<Schedule> {
	
	private static final long serialVersionUID = 1L;

	/**
	 * schedule_id
	 */
	@TableId(value = "schedule_id",type = IdType.UUID)
	private String scheduleId;

	/**
	 * 日程名称
	 */
	private String scheduleName;

	/**
	 * 关联的项目
	 */
	private String projectId;

	/**
	 * 设置任务重复
	 */
	@TableField("`repeat`")
	private String repeat;

	/**
	 * 任务提醒
	 */
	@TableField("`remind`")
	private String remind;

	/**
	 * 地点
	 */
	private String address;

	/**
	 * 备注
	 */
	private String remarks;

	/**
	 * 创建者id
	 */
	private String memberId;

	/**
	 * 参与者
	 */
	private String memberIds;

	/**
	 * yyyy-MM-dd 形式的字符串日期
	 */
	@TableField(exist = false)
	private String date;

	/**
	 * 创建者信息
	 */
	@TableField(exist = false)
	private UserEntity userEntity;

	/**
	 * 所在的项目信息
	 */
	@TableField(exist = false)
	private Project project;

	/**
	 * 参与者
	 */
	@TableField(exist = false)
	private List<UserEntity> joinInfo;

	/**
	 * 隐私模式 (0.正常 1.隐私)
	 */
	private Integer privacyPattern;

	@TableField(exist = false)
	private List<TaskApiBean> bindTasks;

	@TableField(exist = false)
	private List<FileApiBean> bindFiles;

	@TableField(exist = false)
	private List<ScheduleApiBean> bindSchedules;

	@TableField(exist = false)
	private List<ShareApiBean> bindShares;

	/**
	 * 标签的集合
	 */
	@TableField(exist = false)
	private List<Tag> tagList;

	/**
	 * 在日历上创建日程的日期
	 */
	private Long scheduleCalendar;

	/**
	 * 日程得赞数
	 */
	private Integer fabulousCount;

	private String meetingCode;

	/**
	 * 是否在回收站 0 否 1 是
	 */
	private Integer isDel;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private Long createTime;

	/**
	 * 修改时间
	 */
	@TableField(fill = FieldFill.UPDATE)
	private Long updateTime;
	/**
	 * 日程开始时间
	 */
	private Long startTime;

	/**
	 * 日程结束时间
	 */
	private Long endTime;

	/**
	 * 	标记是否是全天日程(0.否 1.是)
	 */
	private int isAllday;

	/**
	 * 判断当前用户是否收藏了该日程
	 */
	@TableField(exist = false)
	private Boolean isCollect;

	/**
	 * 判断当前用户是否对该日程点过赞
	 * @return
	 */
	@TableField(exist = false)
	private Boolean isFabulous;

	/**
	 * 该日程的关联信息
	 */
	@TableField(exist = false)
	private List<Binding> bindings;

	/**
	 * 该日程的日志信息
	 */
	@TableField(exist = false)
	private List<Log> logs;

	/**
	 * 未读消息数
	 */
	@TableField(exist = false)
	private int unReadMsg;

	/**
	 * 创建者id
	 */
	@TableField(exist = false)
	private String userId;

	/**
	 * 创建者名称
	 */
	@TableField(exist = false)
	private String userName;

	/**
	 * 创建者头像
	 */
	@TableField(exist = false)
	private String img;

	/**
	 * 项目名称
	 */
	@TableField(exist = false)
	private String projectName;

	@Override
	protected Serializable pkVal() {
		return this.scheduleId;
	}
}