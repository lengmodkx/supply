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
 * @ClassName ArticleClass
 * @Author lemon lengmodkx@163.com
 * @Discription 文章分类表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "prm_article_class")
public class ArticleClass extends Model<ArticleClass>{

    /**
      * 索引ID
      */
    @TableId(value = "ac_id",type = IdType.AUTO)
    private Integer acId;

    /**
      * 分类标识码
      */
    private String acCode;

    /**
      * 分类名称
      */
    private String acName;

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
        return this.acId;
    }
}
