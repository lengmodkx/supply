package com.art1001.supply.entity.collect;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * collectEntity
 */
@Data
public class PublicCollect extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private String id;

	/**
	 * collect_id
	 */
	private String collect_id;


	/**
	 * 用户id
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
	 * 用户头像
	 */
	private String memberImg;


	/**
	 * 用户名
	 */
	private String memberName;

	/**
	 * 收藏类型  任务，日程 文件 分享
	 */
	private String collect_type;
}