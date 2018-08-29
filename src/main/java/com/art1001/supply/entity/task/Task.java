package com.art1001.supply.entity.task;

import com.alibaba.fastjson.annotation.JSONField;
import com.art1001.supply.entity.base.BaseEntity;

import java.io.*;
import java.util.List;

import com.art1001.supply.entity.binding.Binding;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.user.UserEntity;
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
	 * 任务的日历日期
	 */
	private Long taskCalendar;

	/**
	 * 该任务所在的项目
	 */
	private Project project;

	/**
	 * 该任务的创建者
	 */
	private UserEntity creatorInfo;
	/**
	 * 该任务的执行者
	 */
	private UserEntity executorInfo;

	/**
	 * 该任务的参与者
	 */
	private List<UserEntity> joinInfo;

	/**
	 * 该任务的子任务
	 */

	private List<Task> taskList;

	/**
	 * 任务附件
	 */
	private List<TaskFile> taskFileList;

	public static <T> T clone(T obj) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bout);
		oos.writeObject(obj);

		ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bin);
		return (T) ois.readObject();

		// 说明：调用ByteArrayInputStream或ByteArrayOutputStream对象的close方法没有任何意义
		// 这两个基于内存的流只要垃圾回收器清理对象就能够释放资源，这一点不同于对外部资源（如文件流）的释放
	}

}