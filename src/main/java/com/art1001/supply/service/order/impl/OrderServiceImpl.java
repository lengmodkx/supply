package com.art1001.supply.service.order.impl;

import com.art1001.supply.entity.order.Order;
import com.art1001.supply.mapper.order.OrderMapper;
import com.art1001.supply.service.order.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.SortedMap;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author heshaohua
 * @since 2019-08-31
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {


    @Override
    public int generateOrder(SortedMap<Object, Object> packageParams) {
        Order order = new Order();
        String[] attaches = String.valueOf(packageParams.get("attach")).split(",");
        order.setProductId(Integer.valueOf(attaches[1]));
        order.setStatus(true);
        order.setUserId(attaches[0]);
        order.setCreateTime(System.currentTimeMillis());
        order.setUpdateTime(System.currentTimeMillis());
        return save(order) ? 1:0;
    }
}
