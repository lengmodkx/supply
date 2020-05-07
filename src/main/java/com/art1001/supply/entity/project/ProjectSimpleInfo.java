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
   * 成员id
   */
  private String memberId;
  /**
   * 项目id
   */
  private String projectId;


  @Override
  protected Serializable pkVal() {
    return this.id;
  }
}
