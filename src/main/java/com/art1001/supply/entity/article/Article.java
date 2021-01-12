package com.art1001.supply.entity.article;

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
@TableName(value = "prm_article")
public class Article extends Model<Article>{

    /**
      * 索引id
      */
    @TableId(value = "article_id",type = IdType.UUID)
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
     * 视频地址
     */
    private String videoAddress;

    /**
     * 视频封面
     */
    private String videoCover;

    /**
     * 内容状态 1待发布 2待审核 3已发布
     */
    private Integer state;
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
     * 评论数
     */
    @TableField(exist = false)
    private Integer commentCount;

    /**
     * 评论列表
     */
    @TableField(exist = false)
    private List<Comment>comments;

    @Override
    protected Serializable pkVal() {
        return this.articleId;
    }
}
