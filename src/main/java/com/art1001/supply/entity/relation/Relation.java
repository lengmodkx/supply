package com.art1001.supply.entity.relation;

import com.art1001.supply.entity.task.Task;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * relationEntity
 */
@Data
@TableName("prm_relation")
public class Relation extends Model<Relation> {
	
	private static final long serialVersionUID = 1L;


	/**
	 * relation_id
	 */
	@TableId(value = "relation_id",type = IdType.UUID)
	private String relationId;

	/**
	 * 分组的创建人id
	 */
	@TableField("creator")
	private String creator;

	/**
	 * 名称
	 */
	@TableField("relation_name")
	private String relationName;


	/**
	 * 父级id
	 */
	@TableField("parent_id")
	private String parentId;


	/**
	 * 项目id
	 */
	@TableField("project_id")
	private String projectId;

	/**
	 * 标识是分组还是菜单
	 */
	@TableField("lable")
	private Integer lable;

	/**
	 * 删除分组/菜单 0不删除，1删除
	 */
	@TableField("relation_del")
	private Integer relationDel;

	@TableField(exist = false)
	private List<Task> taskList;

	/**
	 * 菜单的当前顺序
	 */
	@TableField("`order`")
	private Integer order;

	/**
	 * 创建时间
	 */
	@TableField("create_time")
	private Long createTime;

	/**
	 * 修改时间
	 */
	@TableField("update_time")
	private Long updateTime;

	@Override
	protected Serializable pkVal() {
		return this.relationId;
	}
}