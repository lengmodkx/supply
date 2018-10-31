package com.art1001.supply.entity.collect;
import com.art1001.supply.entity.project.Project;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * collectEntity
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName(value = "prm_project_collect")
public class ProjectCollect extends Model<ProjectCollect> {
	
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
	 * 创建时间
	 */
	private Long createTime;
	/**
	 * 修改时间
	 */
	private Long updateTime;

	/**
	 * 是否收藏，0不收藏，1收藏
	 */
	private Integer collectStatus;


	private Project project;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}