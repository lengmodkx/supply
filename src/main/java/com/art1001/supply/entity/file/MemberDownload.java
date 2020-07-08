package com.art1001.supply.entity.file;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("prm_member_download")
public class MemberDownload extends Model<MemberDownload> {

  @Id
  @TableId(value = "id",type = IdType.UUID)
  private String id;

  /**
   * 成员id
   */
  private String memberId;

  /**
   * 文件id
   */
  private String fileId;

  /**
   * 是否删除 0未删除 1已删除
   */
    private Integer isDelete;

  /**
   * 下载时间
   */
  private String downloadTime;

  /**
   * 文件后缀名
   */
  @TableField(exist = false)
  private String ext;

  /**
   * 文件路径
   */
  @TableField(exist = false)
  private String fileUrl;

  @TableField(exist = false)
  private String fileName;

  @TableField(exist = false)
  private String size;

  @TableField(exist = false)
  private String createTime;

  @Override
  protected Serializable pkVal() {
    return this.id;
  }
}
