package com.art1001.supply.entity.organization;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "prm_invitation_from_link")
public class InvitationLink extends Model<InvitationLink> {

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
   * 短链接
   */
  private String shortUrl;
  /**
   * 完整链接
   */
  private String completeUrl;
  /**
   * 邀请人id
   */
  private String memberId;
  /**
   * 到期时间
   */
  private long expireTime;
  /**
   * 创建时间
   */
  private long createTime;
  /**
   * 修改时间
   */
  private long updateTime;

  /**
   * 是否过期
   */
  private Integer isExpire;

  private String hash;


  @Override
  protected Serializable pkVal() {
    return this.id;
  }
}
