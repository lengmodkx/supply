package com.art1001.supply.entity.task;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * Entity
 */
@Data
public class TaskFile extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
	private String id;


	/**
	 * 绑定id
	 */
	private String taskId;


	/**
	 * 其他id
	 */
	private String fileId;


	/**
	 * 关联的类型
	 */
	private String fileName;


	/**
	 * file_ext
	 */
	private String fileExt;


	/**
	 * file_size
	 */
	private String fileSize;


	/**
	 * file_url
	 */
	private String fileUrl;


	/**
	 * file_thumbnail
	 */
	private String fileThumbnail;

}