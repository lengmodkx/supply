package com.art1001.supply.service.order.impl;

import com.art1001.supply.entity.order.OrderDemand;
import com.art1001.supply.mapper.order.OrderDemandMapper;
import com.art1001.supply.service.order.OrderDemandService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @ClassName OrderDemandServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2021/1/25 17:05
 * @Discription 订单需求业务类
 */
@Service
public class OrderDemandServiceImpl extends ServiceImpl<OrderDemandMapper, OrderDemand> implements OrderDemandService {
}
