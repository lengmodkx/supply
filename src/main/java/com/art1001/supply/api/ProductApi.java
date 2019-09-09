package com.art1001.supply.api;


import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.service.product.ProductService;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author heshaohua
 * @since 2019-08-31
 */
@RestController
@RequestMapping("/product")
public class ProductApi {

    @Resource
    private ProductService productService;

    @RequestMapping("list")
    public JSONObject getList(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", productService.list(null));
        return jsonObject;
    }

}

