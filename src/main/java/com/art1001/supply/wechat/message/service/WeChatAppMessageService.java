package com.art1001.supply.wechat.message.service;

import com.art1001.supply.wechat.message.template.WeChatAppMessageTemplate;

/**
 * @author ddm
 * @date 2019/11/7 14:29
 **/
public interface WeChatAppMessageService {

    /**
     * 给单个用户推送小程序消息
     * @param appId 用户在小程序中的唯一标识
     * @param weChatAppMessageTemplate 小程序消息推送请求数据包装对象
     */
    void pushToSingleUser(String appId, WeChatAppMessageTemplate weChatAppMessageTemplate);
}