package com.art1001.supply.entity.order;

import com.art1001.supply.util.LongToDeteSerializer;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName OrderComment
 * @Author lemon lengmodkx@163.com
 * @Discription 订单评价表 订单完成后甲方与平台之间的评价信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "prm_order_comment")
public class OrderComment extends Model<OrderComment>{

    /**
      * 订单评论id
      */
    @TableId(value = "order_comment_id",type = IdType.ASSIGN_UUID)
    private String orderCommentId;

    /**
      * 订单评论id
      */
    private String orderId;

    /**
      * 评论内容
      */
    private String commentContent;

    /**
      * 父级评论id
      */
    private String parentComment;

    /**
      * 1 甲方评价平台 2平台评价甲方
      */
    private Integer parentType;

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
