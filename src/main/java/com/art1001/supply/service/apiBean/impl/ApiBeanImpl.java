package com.art1001.supply.service.apiBean.impl;

import com.art1001.supply.service.apiBean.ApiBean;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.collect.PublicCollectService;

import javax.annotation.Resource;

/**
 * @author heshaohua
 * @Title: ApiBeanImpl
 * @Description: TODO
 * @date 2018/9/25 16:18
 **/
public class ApiBeanImpl implements ApiBean {

    @Resource
    private PublicCollectService publicCollectService;

    @Resource
    private BindingService bindingService;

    /**
     * 更新表字段的json数据
     */
    @Override
    public void updateJSON(String id, Object obj, String type) {
        bindingService.updateJson(id,obj,type);
    }

}
