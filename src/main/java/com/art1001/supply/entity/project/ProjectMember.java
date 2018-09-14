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

	/**
	 * 是否是项目成员，0是，1是拥有者
	 */
	private int memberLabel;

	private int lable;

	/**
	 * 是否接受项目的消息
	 */
	private int IsReceiveMessage;

	/**
	 * 项目成员的角色,默认成员
	 */
	private long rId;
}