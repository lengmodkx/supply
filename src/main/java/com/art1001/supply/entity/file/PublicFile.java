package com.art1001.supply.entity.file;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * 公共文件库Entity
 */
@Data
public class PublicFile extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * 公共文件库
	 */
	private String fileId;


	/**
	 * 文件名
	 */
	private String fileName;


	/**
	 * 文件后缀名
	 */
	private String ext;


	/**
	 * 文件路径
	 */
	private String fileUrl;


	/**
	 * 关联的项目id
	 */
	private String projectId;


	/**
	 * 是否目录
	 */
	private Integer catalog;


	/**
	 * 文件大小
	 */
	private String size;


	/**
	 * 父级id
	 */
	private String parentId;


	/**
	 * 是否删除
	 */
	private Integer fileDel;


	/**
	 * 创建时间
	 */


	/**
	 * 更新时间
	 */


	/**
	 * 如果文件是模型图的话  该字段用于模型图的缩略图
	 */
	private String fileThumbnail;


	/**
	 * 隐私模式
	 */
	private Integer filePrivacy;

}