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

import java.math.BigDecimal;

/**
 * @ClassName OrderDemand
 * @Author lemon lengmodkx@163.com
 * @Discription 需求订单表，双方签署合同达成合作后操作的表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "prm_order_demand")
public class OrderDemand extends Model<OrderDemand>{

    /**
      * 订单id
      */
    @TableId(value = "order_id",type = IdType.ASSIGN_UUID)
    private String orderId;

    /**
      * 订单编号
      */
    private String orderSn;

    /**
      * 支付id
      */
    private String payId;

    /**
      * 退款id
      */
    private String refundId;

    /**
      * 需求id
      */
    private String demandId;

    /**
      * 订单评价id
      */
    private String orderCommentId;

    /**
      * 发票id
      */
    private String invoiceId;

    /**
      * 需求提出人（甲方负责人）id
      */
    private String bidMemberId;

    /**
      * 需求提出人（甲方负责人）名称
      */
    private String bidMemberName;

    /**
      * 需求提出人（甲方负责人）头像
      */
    private String bidMemberImage;

    /**
      * 需求提出企业（甲方）id
      */
    private String bidOrgId;

    /**
      * 需求提出企业（甲方）名称
      */
    private String bidOrgName;

    /**
      * 需求提出企业（甲方）logo
      */
    private String bidOrgImage;

    /**
      * 订单金额
      */
    private BigDecimal orderAmount;

    /**
      * 订单状态 0甲方已取消 1甲方未付款 2甲方已付款
      */
    private Integer orderState;

    /**
      * 订单留言
      */
    private String leaveMessage;

    /**
      * 订单创建时间
      */
    @JsonSerialize(using = LongToDeteSerializer.class)
    private Long createTime;

    /**
      * 订单修改时间
      */
    @JsonSerialize(using = LongToDeteSerializer.class)
    private Long updateTime;

    /**
      * 订单完成时间
      */
    @JsonSerialize(using = LongToDeteSerializer.class)
    private Long finishTime;

}
