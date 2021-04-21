package com.art1001.supply.entity.file;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * fileEntity
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName(value = "prm_file_version")
public class FileVersion extends Model<FileVersion> {
	
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId(value = "version_id",type = IdType.ASSIGN_UUID)
	private String versionId;


	/**
	 * 文件id
	 */
	private String fileId;

	/**
	 * 日志信息  默认：XX 上传于 2018-7-8 14:13
	 */
	private String info;

	/**
	 * 是否主版本，默认新创建的文件为1，一个文件只能有一个主版本
	 */
	private Integer isMaster;

	private String originalFileId;
    @TableField(exist = false)
	private boolean visible;
	@Override
	protected Serializable pkVal() {
		return this.versionId;
	}
}