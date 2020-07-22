package com.art1001.supply.entity.log;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@TableName("prm_log_export_record")
public class LogExportRecord extends Model<LogExportRecord> {

  /**
   * id
   */
  @TableId(value = "id",type = IdType.UUID)
  private String id;
  /**
   * 提交时间
   */
  private long commitTime;
  /**
   *完成时间
   */
  private long completeTime;
  /**
   * 申请人
   */
  private String commitMemberId;

  /**
   * 导出人
   */
  private String exportMemberId;

  /**
   * 筛选条件_开始时间
   */
  private Long conditionStart;

  /**
   * 筛选条件_结束时间
   */
  private Long conditionEnd;
  /**
   * 状态 0 未完成 1已完成
   */
  private Integer status;


  @Override
  protected Serializable pkVal() {
    return this.id;
  }
}
