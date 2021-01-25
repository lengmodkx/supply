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
 * @ClassName OrderRefund
 * @Author lemon lengmodkx@163.com
 * @Discription 退款表，甲方从平台提交退款请求时会操作这个表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "prm_order_refund")
public class OrderRefund extends Model<OrderRefund>{

    /**
      * 退款id
      */
    @TableId(value = "refund_id",type = IdType.ASSIGN_UUID)
    private String refundId;

    /**
      * 订单id
      */
    private String orderId;

    /**
      * 支付id
      */
    private String payId;

    /**
      * 退款金额
      */
    private BigDecimal refundAmount;

    /**
      * 退款状态 0待退款 1已退款 2已取消
      */
    private Integer refundState;

    /**
      * 退款时间
      */
    @JsonSerialize(using = LongToDeteSerializer.class)
    private Long refundTime;

}
