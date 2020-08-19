package com.art1001.supply.entity.chat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;



/**
 * @author lemon
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("prm_hx_chat_notice")
public class HxChatNotice extends Model<HxChatNotice> {

  @Id
  @TableId(value = "id",type = IdType.UUID)
  private String id;

  /**
   * 消息发给谁
   */
  @TableField("news_to_user")
  private String newsToUser;

  /**
   * 消息接收方电话
   */
  @TableField(exist = false)
  private String newsToUserAccountName;

  /**
   * 消息来自于谁
   */
  @TableField("news_from_user")
  private String newsFromUser;

  /**
   * 消息发送方电话
   */
  @TableField(exist = false)
  private String newsFromUserAccountName;
  /**
   * 消息
   */
  @TableField("news_content")
  private String newsContent;

  /**
   * 消息来源 0 单聊  1群聊  2聊天室
   */
  @TableField("news_address")
  private Integer newsAddress;

  /**
   * 消息通知数
   */
  @TableField("news_count")
  private Integer newsCount;

  /**
   * 消息状态 0未读 1已读
   */
  @TableField("news_handle")
  private Integer newsHandle;

  /**
   * 环信群组id
   */
  @TableField("hx_group_id")
  private String hxGroupId;

  /**
   * 群组id
   */
  @TableField("group_id")
  private String groupId;

  /**
   * 创建时间
   */
  @TableField("create_time")
  private long createTime;

  /**
   * 修改时间
   */
  @TableField("update_time")
  private long updateTime;


  @Override
  protected Serializable pkVal() {
    return this.id;
  }
}
