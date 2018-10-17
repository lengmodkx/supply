package com.art1001.supply.entity.project;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * projectMemberEntity
 */
@Data
@ToString
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
	@TableField("r_id")
	private Integer roleId;

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

	/**
	 * 用户在该项目中的默认分组
	 * @return
	 */
	@TableField("default_group")
	private String defaultGroup;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}