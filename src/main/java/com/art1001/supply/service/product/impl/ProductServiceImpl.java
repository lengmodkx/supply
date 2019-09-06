package com.art1001.supply.service.product.impl;

import com.art1001.supply.entity.product.Product;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.mapper.product.ProductMapper;
import com.art1001.supply.service.product.ProductService;
import com.art1001.supply.service.user.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.SortedMap;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author heshaohua
 * @since 2019-08-31
 */
@Slf4j
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Resource
    private UserService userService;

    @Override
    public int authorization(SortedMap<Object, Object> packageParams) {
        UserEntity userEntity = new UserEntity();
        String[] attaches = String.valueOf(packageParams.get("attach")).split(",");
        userEntity.setUserId(attaches[0]);
        userEntity.setUpdateTime(new Date());
        userEntity.setVip(Integer.valueOf(attaches[1]));
        return userService.updateById(userEntity) ? 1:0;
    }
}
