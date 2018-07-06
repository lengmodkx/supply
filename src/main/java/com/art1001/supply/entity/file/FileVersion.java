package com.art1001.supply.entity.file;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * fileEntity
 */
@Data
public class FileVersion extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
	private String id;


	/**
	 * file_id
	 */
	private String fileId;


	/**
	 * 当前版本的文件链接
	 */
	private String fileUrl;


	/**
	 * 日志信息  默认：XX 上传于 2018-7-8 14:13
	 */
	private String info;


	/**
	 * 文件大小
	 */
	private String fileSize;


	/**
	 * 是否主版本，默认新创建的文件为1，一个文件只能有一个主版本
	 */
	private Integer isMaster;

}