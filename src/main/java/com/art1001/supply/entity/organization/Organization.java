package com.art1001.supply.entity.organization;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.validation.organization.SaveOrg;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

/**
 * organizationEntity
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName(value = "prm_organization")
public class Organization extends Model<Organization> {
	
	private static final long serialVersionUID = 1L;


	/**
	 * 企业id
	 */
	@TableId(value = "organization_id",type = IdType.UUID)
	private String organizationId;


	/**
	 * 企业名称
	 */
	@NotEmpty(message = "企业名称不能为空!",groups = SaveOrg.class)
	private String organizationName;


	/**
	 * 企业头像
	 */
	private String organizationImage;


	/**
	 * 企业规模
	 */
	@NotEmpty(message = "企业简介不能为空!",groups = SaveOrg.class)
	private String organizationDes;



	/**
	 * 企业简介
	 */
	private String organizationIntro;


	/**
	 * 0私有企业1公开企业
	 */
	private Integer isPublic;


	/**
	 * 企业拥有者
	 */
	private String organizationMember;

	/**
	 * 企业联系人
	 */
	@NotEmpty(message = "联系人不能为空",groups = SaveOrg.class)
	private String contact;

	/**
	 * 企业联系人手机号
	 */
	@Pattern(regexp = "^[1][3,4,5,7,8][0-9]{9}$",groups = SaveOrg.class)
	private String contactPhone;

	/**
	 * 创建时间
	 */
	private Long createTime;

	/**
	 * 修改时间
	 */
	private Long updateTime;

	/**
	 * 该企业下的所有项目
	 */
	@TableField(exist = false)
	private List<Project> projects;

	/**
	 * 用户参与该企业的所有项目
	 */
	@TableField(exist = false)
	private List<Project> joinProjects;

	/**
	 * 是否是当前用户所在的企业
	 */
	@TableField(exist = false)
	private Boolean isSelection;

	@Override
	protected Serializable pkVal() {
		return this.organizationId;
	}
}