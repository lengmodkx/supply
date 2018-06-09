package com.art1001.supply.entity.collect;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;

import com.art1001.supply.entity.project.Project;
import lombok.Data;

/**
 * collectEntity
 */
@Data
public class ProjectCollect extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
	private String id;


	/**
	 * 用户id
	 */
	private String memberId;


	/**
	 * 项目id
	 */
	private String projectId;

	/**
	 * 用户头像
	 */
	private String memberImg;


	/**
	 * 用户名
	 */
	private String memberName;


	/**
	 * 是否收藏，0不收藏，1收藏
	 */
	private Integer collectStatus;


	private Project project;
}