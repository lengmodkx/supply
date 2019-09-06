package com.art1001.supply.service.order;

import com.art1001.supply.entity.order.Order;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.SortedMap;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author heshaohua
 * @since 2019-08-31
 */
public interface OrderService extends IService<Order> {

    /**
     * 根据微信支付后的回调生成订单
     * @param packageParams 微信回调请求中的参数
     * @return 是否生成成功
     */
    int generateOrder(SortedMap<Object, Object> packageParams);
}
