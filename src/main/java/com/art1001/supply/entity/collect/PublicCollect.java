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
	private String publicId;


	/**
	 * 用户id
	 */
	private String memberId;


	/**
	 * 项目id
	 */
	private String projectId;


	/**
	 * 收藏类型  任务，日程 文件 分享
	 */
	private String collectType;
}