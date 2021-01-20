package com.art1001.supply.entity.content;

import com.art1001.supply.util.LongToDeteSerializer;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName Reply
 * @Author lemon lengmodkx@163.com
 * @Discription 问答相关 回答表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "prm_reply")
public class Reply extends Model<Reply>{

    /**
      * 回答id
      */
    @TableId(value = "reply_id",type = IdType.ASSIGN_UUID)
    private String replyId;

    /**
      * 问题id
      */
    private String questionId;

    /**
      * 回答人id
      */
    private String replyMemberId;
    /**
     * 回答人名字
     */
    @TableField(exist = false)
    private String replyMemberName;
    /**
     * 回答人头像
     */
    @TableField(exist = false)
    private String replyMemberImage;

    /**
      * 回答内容
      */
    private String replyContent;

    /**
      * 是否匿名（0否 1是）
      */
    private Integer isIncognito;

    /**
      * 是否是草稿（0否 1是）
      */
    private Integer isDraft;

    /**
      * 是否删除（0否 1是）
      */
    private Integer isDel;

    /**
      * 创建时间
      */
    @JsonSerialize(using = LongToDeteSerializer.class)
    private Long createTime;

    /**
      * 修改时间
      */
    @JsonSerialize(using = LongToDeteSerializer.class)
    private Long updateTime;

}
