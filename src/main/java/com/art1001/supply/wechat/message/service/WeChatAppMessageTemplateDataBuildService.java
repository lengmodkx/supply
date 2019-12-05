package com.art1001.supply.wechat.message.service;

import com.art1001.supply.wechat.message.template.TemplateData;

import java.util.Map;

/**
 * @author ddm
 * @description
 * @date 2019/12/5 14:44
 **/
@FunctionalInterface
public interface WeChatAppMessageTemplateDataBuildService {

    /**
     * 构造出要推送的map数据
     * @param userId 用户id
     * @return map数据
     */
    Map<String, TemplateData> buildData(String userId);

}