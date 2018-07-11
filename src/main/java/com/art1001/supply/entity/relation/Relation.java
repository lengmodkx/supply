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
	 * 菜单的当前顺序
	 */
	private Integer order;

	public Relation(){

	}

	public Relation(String relationId, String relationName, String parentId, Integer lable, Integer relationDel,Long createTime,Long endTime,Integer order) {
		this.relationId = relationId;
		this.relationName = relationName;
		this.parentId = parentId;
		this.projectId = projectId;
		this.lable = lable;
		this.relationDel = relationDel;
		this.setCreateTime(createTime);
		this.setEndTime(endTime);
		this.order = order;
	}
}