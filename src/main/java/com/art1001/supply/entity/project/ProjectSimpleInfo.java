package com.art1001.supply.entity.project;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@TableName("prm_project_simple_info")
public class ProjectSimpleInfo extends Model<ProjectSimpleInfo> {

  /**
   * 主键id
   */
  @TableId(value = "id",type = IdType.UUID)
  private String id;
  /**
   * 被修改人的成员id
   */
  private String modifyId;

  /**
   * 修改人的成员id
   */
  private String updateId;
  /**
   * 项目id
   */
  private String projectId;

  /**
   * 企业id
   */
  private String organizationId;

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
