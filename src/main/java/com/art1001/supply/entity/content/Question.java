package com.art1001.supply.entity.content;

import com.art1001.supply.util.LongToDeteSerializer;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName Question
 * @Author lemon lengmodkx@163.com
 * @Discription 问答相关 问题表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "prm_question",autoResultMap = true)
public class Question extends Model<Question>{

    /**
      * 问题id
      */
    @TableId(value = "question_id",type = IdType.ASSIGN_UUID)
    private String questionId;

    /**
      * 问题
      */
    private String questionContent;

    /**
      * 问题描述
      */
    private String questionDepict;

    /**
      * 问题描述图片
      */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<String> questionDepictImages;

    /**
      * 问题提出人id
      */
    private String questionMemberId;

    /**
     * 问题提出人名字
     */
    @TableField(exist = false)
    private String questionMemberName;

    /**
     * 问题提出人头像
     */
    @TableField(exist = false)
    private String questionMemberImage;

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
     * 回答总数
     */
    @TableField(exist = false)
    private Integer replyCount;

}
