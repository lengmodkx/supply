package com.art1001.supply.aliyun.message.service;

/**
 * @author ddm
 * @date 2019/11/21 15:26
 **/
public interface MessageService {

    /**
     * 生成code发送短信验证码
     * @param phone 移动手机号码
     */
    void sendCode(String userId, String phone);
}