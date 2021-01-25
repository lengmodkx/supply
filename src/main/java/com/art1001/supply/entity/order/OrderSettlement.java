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
 * @ClassName OrderSettlement
 * @Author lemon lengmodkx@163.com
 * @Discription 订单结算表，平台将钱打给乙方后操作的表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "prm_order_settlement")
public class OrderSettlement extends Model<OrderSettlement>{

    /**
      * 结算id
      */
    @TableId(value = "settlement_id",type = IdType.ASSIGN_UUID)
    private String settlementId;

    /**
      * 需求id
      */
    private String demandId;

    /**
      * 结算总金额
      */
    private BigDecimal settlementAmount;

    /**
      * 一期结算金额
      */
    private BigDecimal firstAmount;

    /**
      * 二期结算金额
      */
    private BigDecimal secondAmount;

    /**
      * 三期结算金额
      */
    private BigDecimal thirdAmount;

    /**
      * 结算状态 0未结算 1一期已结算 2二期已结算 3三期已结算 4全部结算
      */
    private Integer settlementState;

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
