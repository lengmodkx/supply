package com.art1001.supply.entity.file;

import java.io.Serializable;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

/**
 * fileEntity
 */
@Data
@ToString
public class FileVersion extends Model<FileVersion> {
	
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
	private String id;


	/**
	 * 文件id
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

	/** 创建时间
	 *
	 */
	private Long createTime;
	/** 修改时间*/
	private Long updateTime;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}