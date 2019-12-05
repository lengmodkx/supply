package com.art1001.supply.wechat.message.service;

import com.art1001.supply.wechat.message.template.WeChatAppMessageTemplate;

import java.util.List;

/**
 * @author ddm
 * @date 2019/11/7 14:29
 **/
public interface WeChatAppMessageService {

    /**
     * 给单个用户推送小程序消息
     * @param userId 用户id
     * @param weChatAppMessageTemplate 小程序消息推送请求数据包装对象
     */
    void pushToSingleUser(String userId, WeChatAppMessageTemplate weChatAppMessageTemplate);

    /**
     * 推送多条消息到微信小程序用户
     * @param userIdList 用户id集合
     * @param weChatAppMessageTemplate 用户消息模板
     * @param dataBuild 推送数据构造器
     */
    void pushToMultipleUsers(List<String> userIdList, WeChatAppMessageTemplate weChatAppMessageTemplate, WeChatAppMessageTemplateDataBuildService dataBuild);
}