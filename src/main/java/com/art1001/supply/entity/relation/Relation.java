package com.art1001.supply.entity.relation;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import java.util.List;

import com.art1001.supply.entity.task.Task;
import lombok.Data;

/**
 * relationEntity
 */
@Data
public class Relation extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * relation_id
	 */
	private String relationId;

	/**
	 * 分组的创建人id
	 */
	private String creator;

	/**
	 * 名称
	 */
	private String relationName;


	/**
	 * 父级id
	 */
	private String parentId;


	/**
	 * 项目id
	 */
	private String projectId;

	/**
	 * 标识是分组还是菜单
	 */
	private Integer lable;

	/**
	 * 删除分组/菜单 0不删除，1删除
	 */
	private Integer relationDel;

	private List<Task> taskList;

	/**
	 * 分组 或者 菜单的描述
	 */
	private String describe;

	/**
	 * 菜单的当前顺序
	 */
	private Integer order;

	/**
	 * 默认分组
	 */
	private int defaultGroup;

}