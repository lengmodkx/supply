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
 * @ClassName OrderPay
 * @Author lemon lengmodkx@163.com
 * @Discription 支付表，甲方将金额提交给平台后会操作这个表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "prm_order_pay")
public class OrderPay extends Model<OrderPay>{

    /**
      * 订单id
      */
    @TableId(value = "pay_id",type = IdType.ASSIGN_UUID)
    private String payId;

    /**
      * 订单id
      */
    private String orderId;

    /**
      * 支付类型 1微信 2支付宝 3银联
      */
    private Integer payType;

    /**
      * 支付金额
      */
    private BigDecimal payAmount;

    /**
      * 支付状态 0待支付 1已支付 2已取消
      */
    private Integer payState;

    /**
      * 支付时间
      */
    @JsonSerialize(using = LongToDeteSerializer.class)
    private Long payTime;

    /**
      * 支付人id
      */
    private String payMemberId;

    /**
      * 支付公司id
      */
    private String payOrganizationId;

    /**
      * 支付留言
      */
    private String leaveMessage;

}
