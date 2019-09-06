package com.art1001.supply.service.product;

import com.art1001.supply.entity.product.Product;
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
public interface ProductService extends IService<Product> {

    /**
     * 微信支付成功后，生成订单，以及执行授权逻辑。
     * @param packageParams 微信支付回调请求中的参数
     * @return 是否乘
     */
    int authorization(SortedMap<Object, Object> packageParams);
}
