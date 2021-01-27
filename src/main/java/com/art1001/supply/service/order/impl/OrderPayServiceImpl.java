package com.art1001.supply.service.order.impl;

import com.art1001.supply.entity.order.OrderPay;
import com.art1001.supply.mapper.order.OrderPayMapper;
import com.art1001.supply.service.order.OrderPayService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @ClassName OrderPayServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2021/1/25 17:07
 * @Discription 订单支付核心业务类
 */
@Service
public class OrderPayServiceImpl extends ServiceImpl<OrderPayMapper, OrderPay> implements OrderPayService {
}
