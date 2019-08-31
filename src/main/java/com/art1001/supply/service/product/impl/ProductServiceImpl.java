package com.art1001.supply.service.product.impl;

import com.art1001.supply.entity.product.Product;
import com.art1001.supply.mapper.product.ProductMapper;
import com.art1001.supply.service.product.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author heshaohua
 * @since 2019-08-31
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

}
