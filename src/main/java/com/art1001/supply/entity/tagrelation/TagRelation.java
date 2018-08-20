package com.art1001.supply.entity.tagrelation;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * Entity
 */
@Data
public class TagRelation extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
	private String id;


	/**
	 * 标签id
	 */
	private long tagId;


	/**
	 * 任务id
	 */
	private String taskId;


	/**
	 * 文件id
	 */
	private String fileId;


	/**
	 * 日程id
	 */
	private String scheduleId;


	/**
	 * 分享id
	 */
	private String shareId;

}