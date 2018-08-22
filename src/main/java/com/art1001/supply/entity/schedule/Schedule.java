package com.art1001.supply.entity.schedule;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import java.util.List;

import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.user.UserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * scheduleEntity
 */
@Data
public class Schedule extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * schedule_id
	 */
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
	private String repeat;


	/**
	 * 任务提醒
	 */
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
	 * 标签
	 */
	private String tagId;

	/**
	 * 创建者id
	 */
	private String memberId;
	/**
	 * 参与者
	 */
	private String memberIds;
	/**
	 * 创建者信息
	 */
	private UserEntity userEntity;

	/**
	 * 所在的项目信息
	 */
	private Project project;

	/**
	 * 参与者
	 */
	private List<UserEntity> joinInfo;

	/**
	 * 是否是全天
	 */
	private Integer isAllday;

	/**
	 * 标签的集合
	 */
	private List<Tag> tagList;

	/**
	 * 标签集合
	 */
	private List<Tag> tags;

	/**
	 * 在日历上创建日程的日期
	 */
	private Long scheduleCalendar;

	/**
	 * 是否删除
	 */
	private int isDel;


}