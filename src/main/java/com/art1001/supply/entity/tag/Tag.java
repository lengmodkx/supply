package com.art1001.supply.entity.tag;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.task.Task;
import lombok.Data;

/**
 * tagEntity
 */
@Data
public class Tag extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * tag_id
	 */
	private Long tagId;

	/**
	 * 标签名称
	 */
	private String tagName;

	/**
	 * 背景颜色
	 */
	private String bgColor;

	/**
	 * 标签创建人
	 */
	private String memberId;

	/**
	 * 项目
	 */
	private String projectId;

	/**
	 * 任务
	 */
	private List<Task> taskList;

	/**
	 * 日程
	 */
	private List<Schedule> scheduleList;

	/**
	 * 分享
	 */
	private List<Share> shareList;

	/**
	 * 文件
	 */
	private List<File> fileList;
	/**
	 * 判断标签是否选中
	 */
	private boolean flag;


}