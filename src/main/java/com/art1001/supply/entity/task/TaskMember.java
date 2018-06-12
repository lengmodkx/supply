package com.art1001.supply.entity.task;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Data;

/**
 * taskMemberEntity
 */
@Data
public class TaskMember extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
	private String id;


	/**
	 * 成员id
	 */
	private String memberId;


	/**
	 * 功能菜单id
	 */
	private String publicId;


	/**
	 * 关联的类型
	 */
	private String publicType;


	/**
	 * 用户名
	 */
	private String memberName;


	/**
	 * 头像
	 */
	private String memberImg;


	/**
	 * 任务角色
	 */
	private String type;


	/**
	 * 创建时间
	 */
	private Long createTime;

	/**
	 * 更新时间
	 */
	private Long updateTime;

}