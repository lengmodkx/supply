package com.art1001.supply.service.order;

import com.art1001.supply.entity.order.OrderDemand;
import com.baomidou.mybatisplus.extension.service.IService;


public interface OrderDemandService extends IService<OrderDemand> {

    /**
     * 添加订单留言
     * @param orderId             订单id
     * @param leaveMessage        订单留言
     * @return
     */
    void addLeaveMessage(String orderId, String leaveMessage);
}
