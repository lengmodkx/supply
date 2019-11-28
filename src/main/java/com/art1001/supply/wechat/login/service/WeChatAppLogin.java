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

    /**
     * 绑定小程序手机号
     * @param data 加密数据
     * @param iv 偏移量
     * @param code 登录code
     * @return 数据集
     * @throws Exception 解密一样抛出
     */
    Map bindPhone(String data, String iv, String code) throws Exception;
}