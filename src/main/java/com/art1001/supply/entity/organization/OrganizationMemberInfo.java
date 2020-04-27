package com.art1001.supply.entity.organization;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@Data
@TableName(value = "prm_organization_member_info")
public class OrganizationMemberInfo extends Model<OrganizationMemberInfo> {

  /**
   *主键id
   */
  @TableId(value = "id",type = IdType.UUID)
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
       * 企业id
   */
  private String organizationId;
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
   * 员工类型 1:成员 2:拥有者 3:管理员
   */
  @TableField(value = "memberLabel")
  private String memberLabel;
  /**
   * 职位
   */
  private String job;
  /**
   * 部门id
   */
  @TableField(value = "deptId")
  private String deptId;
  /**
   * 部门名称
   */
  @TableField(value = "deptName")
  private String deptName;
  /**
   * 上级部门id
   */
  @TableField(value = "parentId")
  private String parentId;
  /**
   * 上级部门名称
   */
  @TableField(value = "parentName")
  private String parentName;
  /**
   * 创建时间
   */
  @TableField(value = "createTime")
  private String createTime;
  /**
   * 修改时间
   */
  @TableField(value = "updateTime")
  private String updateTime;

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
