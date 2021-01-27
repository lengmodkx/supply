package com.art1001.supply.service.order.impl;

import com.art1001.supply.entity.order.OrderRefund;
import com.art1001.supply.mapper.order.OrderRefundMapper;
import com.art1001.supply.service.order.OrderRefundService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @ClassName OrderRefundServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2021/1/25 17:13
 * @Discription 订单退款核心业务类
 */
@Service
public class OrderRefundServiceImpl extends ServiceImpl<OrderRefundMapper, OrderRefund> implements OrderRefundService {
}
