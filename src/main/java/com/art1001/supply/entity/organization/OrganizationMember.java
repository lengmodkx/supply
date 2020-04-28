package com.art1001.supply.entity.organization;

import com.art1001.supply.entity.partment.Partment;
import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author 汪亚锋
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName(value = "prm_organization_member")
public class OrganizationMember extends Model<OrganizationMember> {
	
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
	@TableId(value = "id",type = IdType.UUID)
	private String id;


	/**
	 * 企业id
	 */
	private String organizationId;


	/**
	 * 部门id
	 */
	private String partmentId;

	/**
	 * 项目id
	 */
	private String projectId;

	/**
	 * 会员id
	 */
	private String memberId;


	/**
	 * 是否是企业拥有着，0是成员 1是拥有着
	 */
	private Integer organizationLable;


	/**
	 * 是否是部门拥有着，0是成员 1是拥有着
	 */
	private Integer partmentLable;

	/**
	 * 企业用户是否被停用 0停用，1启用
	 */
	private Integer memberLock;

	/**
	 * 用户实体
	 */
	@TableField(exist = false)
	private UserEntity userEntity;

	@TableField(exist = false)
	private Partment partment;
	/**
	 * 创建时间
	 */
	private Long createTime;
	/**
	 * 修改时间
	 */
	private Long updateTime;

	/**
	 * 是否是当前用户所在的企业
	 */
	private Boolean userDefault;

	/**
	 * 是否是企业员工  1为是  0为外部员工
	 */
	private Integer other;

	/**
	 * 名字
	 */
	@TableField(value = "userName")
	private String userName;

	/**
	 * 邮箱
	 */
	@TableField(value = "memberEmail")
	private String memberEmail;

	/**
	 * 生日
	 */
	private String birthday;

	/**
	 * 入职时间
	 */
	@TableField(value = "entry_time")
	private String entryTime;

	/**
	 * 电话号
	 */
	private String phone;

	/**
	 * 办公地点
	 */
	private String address;

	/**
	 * 员工身份
	 */
	@TableField(value = "memberLabel")
	private String memberLabel;

	/**
	 * 职位
	 */
	private String job;

	/**
	 * 用户头像
	 */
	private String image;

	/**
	 * 司龄
	 */
	@TableField(exist = false)
	private String stayComDate;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}