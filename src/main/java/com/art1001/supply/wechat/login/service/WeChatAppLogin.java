package com.art1001.supply.wechat.login.service;

import java.util.Map;

/**
 * @author ddm
 * @date 2019/11/20 14:36
 **/
public interface WeChatAppLogin {

    /**
     * 微信小程序登录
     * @param code 小程序登录用的code码
     * @return 登录信息
     */
    Map<String, Object> login(String code);
}