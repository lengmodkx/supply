package com.art1001.supply.entity.project;

import com.art1001.supply.entity.base.BaseEntity;
import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

/**
 * projectMemberEntity
 */
@Data
@ToString
public class ProjectMember extends Model<ProjectMember> {
	
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
	 * 是否收藏该项目
	 */
	private int collect;

	/**
	 * 项目成员的角色,默认成员
	 */
	private Integer rId;

	/**
	 * 创建时间
	 */
	private Long createTime;
	/**
	 * 修改时间
	 */
	private Long updateTime;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}