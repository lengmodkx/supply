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
 * @ClassName Comment
 * @Author lemon lengmodkx@163.com
 * @Discription 评论表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "prm_comment")
public class Comment extends Model<Comment>{

    /**
      * 评论id
      */
    @TableId(value = "comment_id",type = IdType.ASSIGN_UUID)
    private String commentId;

    /**
      * 评论内容
      */
    private String commentName;

    /**
      * 评论文章
      */
    private String articleId;

    /**
      * 评论人id
      */
    private String memberId;

    /**
     * 评论人昵称
     */
    @TableField(exist = false)
    private String memberName;

    /**
     * 评论人头像
     */
    @TableField(exist = false)
    private String memberImage;

    /**
      * 评论状态 0未审核 1审核已通过 2审核未通过
      */
    private Integer commentState;

    /**
      * 是否删除 0未删除 1已删除
      */
    private Integer isDel;

    /**
     * 审核失败说明
     */
    private String checkFailReason;

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
