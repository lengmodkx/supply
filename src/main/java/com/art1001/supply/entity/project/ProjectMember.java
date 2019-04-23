package com.art1001.supply.entity.project;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * projectMemberEntity
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName("prm_project_member")
public class ProjectMember extends Model<ProjectMember> {
	
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
	@TableId(value = "id", type = IdType.UUID)
	private String id;


	/**
	 * 项目id
	 */
	@TableField("project_id")
	private String projectId;


	/**
	 * 会员id
	 */
	@TableField("member_id")
	private String memberId;


	/**
	 * 用户名
	 */
	@TableField(exist = false)
	private String memberName;


	/**
	 * 头像
	 */
	@TableField(exist = false)
	private String memberImg;


	/**
	 * 手机号
	 */
	@TableField(exist = false)
	private String memberPhone;


	/**
	 * 邮箱
	 */
	@TableField(exist = false)
	private String memberEmail;

	@TableField(exist = false)
	private Project project;

	/**
	 * 是否是项目成员，0是，1是拥有者
	 */
	@TableField("member_label")
	private Integer memberLabel;

	@TableField(exist = false)
	private Integer lable;

	/**
	 * 是否接受项目的消息
	 */
	@TableField("is_receiveMessage")
	private Integer isReceiveMessage;

	/**
	 * 是否收藏该项目
	 */
	@TableField("collect")
	private Integer collect;

	/**
	 * 项目成员的角色,默认成员
	 */
	@TableField("role_id")
	private Integer roleId;

	/**
	 * 创建时间
	 */
	@TableField(value = "create_time",fill = FieldFill.INSERT)
	private Long createTime;

	/**
	 * 修改时间
	 */
	@TableField(value = "update_time",fill = FieldFill.UPDATE)
	private Long updateTime;

	/**
	 * 用户在该项目中的默认分组
	 * @return
	 */
	@TableField("default_group")
	private String defaultGroup;

	/**
	 * 默认视图
	 * 0:列表 1:看板 2:时间
	 */
	private int defaultView;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}