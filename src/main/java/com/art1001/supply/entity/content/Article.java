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

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName Article
 * @Author lemon lengmodkx@163.com
 * @Discription 文章表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "prm_article",autoResultMap = true)
public class Article extends Model<Article>{

    /**
      * 索引id
      */
    @TableId(value = "article_id",type = IdType.ASSIGN_UUID)
    private String articleId;

    /**
      * 分类id
      */
    private Integer acId;

    /**
      * 是否显示，0为否，1为是，默认为1
      */
    private Integer articleShow;

    /**
     * 作者id
     */
    private String memberId;

    /**
     * 作者头像
     */
    @TableField(exist = false)
    private String memberImage;

    /**
     * 用户名
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 封面展示 0为不展示 1单图展示 2三图展示
     */
    private Integer coverShow;

    /**
     * 封面展示图片
     */
    private String coverImages;

    /**
      * 文章标题
      */
    private String articleTitle;

    /**
      * 文章内容
      */
    private String articleContent;

    /**
     * 无html标签的内容
     */
    @TableField(value ="article_pure_content" )
    private String articlePureContent;

    /**
      * 0:未删除;1.已删除
      */
    private Integer isDel;

    /**
     * 微头条内容
     */
    private String headlineContent;

    /**
     * 微头条图片
     */
    private String headlineImages;

    /**
     * 视频名称
     */
    private String videoName;

    /**
     * 视频本地名称
     */
    private String videoLocal;
    /**
     * 视频地址
     */
    private String videoAddress;

    /**
     * 视频封面
     */
    private String videoCover;

    /**
     * 内容状态 1待审核 2审核通过 3审核未通过
     */
    private Integer state;

    /**
     * 审核失败原因
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

    /**
     * 评论总数
     */
    @TableField(exist = false)
    private Integer commentCount;

    /**
     * 评论待通过总数
     */
    @TableField(exist = false)
    private Integer commentNotCheckCount;

    /**
     * 评论已通过总数
     */
    @TableField(exist = false)
    private Integer commentIsCheckCount;

    /**
     * 评论未通过总数
     */
    @TableField(exist = false)
    private Integer commentFailCheckCount;
    /**
     * @人的id
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<String> mentionIds;

    /**
     * 话题id
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<String> topicIds;


    @Override
    protected Serializable pkVal() {
        return this.articleId;
    }
}
