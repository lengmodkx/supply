package com.art1001.supply.entity.tag;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
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
	 * 项目id
	 */
	private String projectId;

	/**
	 * 任务id
	 */
	private String taskId;

	/**
	 * 日程id
	 */
	private String scheduleId;

	/**
	 * 分享id
	 */
	private String shareId;


	private boolean flag;
}