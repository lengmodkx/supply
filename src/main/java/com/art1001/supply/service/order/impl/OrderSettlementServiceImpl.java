package com.art1001.supply.service.order.impl;

import com.art1001.supply.entity.order.OrderSettlement;
import com.art1001.supply.mapper.order.OrderSettlementMapper;
import com.art1001.supply.service.order.OrderSettlementService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @ClassName OrderSettlementServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2021/1/25 17:15
 * @Discription 订单结算核心业务类
 */
@Service
public class OrderSettlementServiceImpl extends ServiceImpl<OrderSettlementMapper, OrderSettlement>implements OrderSettlementService {
}
