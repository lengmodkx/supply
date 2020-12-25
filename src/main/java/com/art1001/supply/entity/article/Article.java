package com.art1001.supply.entity.article;

import com.art1001.supply.util.LongToDeteSerializer;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

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
    private String acId;

    /**
      * 是否显示，0为否，1为是，默认为1
      */
    private Integer articleShow;

    /**
     * 作者id
     */
    private String memberId;

    /**
     * 封面展示 0为不展示 1单图展示 2三图展示
     */
    private Integer coverShow;

    /**
     * 封面展示图片
     */
    private String coverImages;

    /**
      * 标题
      */
    private String articleTitle;

    /**
      * 内容
      */
    private String articleContent;

    /**
      * 0:未删除;1.已删除
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

    @Override
    protected Serializable pkVal() {
        return this.articleId;
    }
}
