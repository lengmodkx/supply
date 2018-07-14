package com.art1001.supply.entity.binding;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * Entity
 */
@Data
public class Binding extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * 关联关系id
	 */
	private String id;


	/**
	 * 任务，日程，文件，分享id
	 */
	private String publicId;


	/**
	 * 任务，日程，文件，分享id
	 */
	private String bindId;


	/**
	 * public_type
	 */
	private String publicType;


	/**
	 * 创建时间
	 */
	private Long createTime;

	/**
	 * 更新时间
	 */
	private Long updateTime;


	/**
	 * 关联目标的头像(任务执行者头像,文件头像,日程执行者等)
	 */
	private String image;


	/**
	 * 关联目标的所在项目
	 */
	private String projectId;


	/**
	 * 关联任务时的任务分组
	 */
	private String groupId;


	/**
	 * 关联任务时的任务菜单
	 */
	private String menuId;


	/**
	 * 分享的链接 或者是 日常的时间
	 */
	private String shareLink;

	/**
	 * 绑定该目标的名称
	 */
	private String bindName;

	/**
	 * 关联目标所在的项目名称
	 */
	private String projectName;

	/**
	 * 关联目标所在的分组名称
	 */
	private String groupName;

	/**
	 * 关联目标所在的菜单名称
	 */
	private String menuName;


}