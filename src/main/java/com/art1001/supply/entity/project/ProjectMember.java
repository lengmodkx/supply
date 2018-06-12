package com.art1001.supply.entity.project;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * projectMemberEntity
 */
@Data
public class ProjectMember extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
	private String id;


	/**
	 * 项目id
	 */
	private String projectId;


	/**
	 * 会员id
	 */
	private String memberId;


	/**
	 * 用户名
	 */
	private String memberName;


	/**
	 * 头像
	 */
	private String memberImg;


	/**
	 * 手机号
	 */
	private String memberPhone;


	/**
	 * 邮箱
	 */
	private String memberEmail;

	private Project project;
}