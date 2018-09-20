package com.art1001.supply.entity.task;
import java.io.Serializable;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * Entity
 */
@Data
public class TaskFile extends Model<TaskFile> {
	
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
	private String id;


	/**
	 * 任务id
	 */
	private String taskId;

	/**
	 * 文件名称
	 */
	private String fileName;


	/**
	 * 文件后缀
	 */
	private String fileExt;


	/**
	 * 文件大小
	 */
	private String fileSize;


	/**
	 * 文件阿里云路径
	 */
	private String fileUrl;


	/**
	 * 模型文件缩略图路径
	 */
	private String fileThumbnail;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}