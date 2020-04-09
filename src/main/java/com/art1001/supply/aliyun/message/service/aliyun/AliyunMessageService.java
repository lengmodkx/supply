package com.art1001.supply.aliyun.message.service.aliyun;

import com.art1001.supply.aliyun.message.service.MessageService;

/**
 * @author ddm
 * @description
 * @date 2019/11/21 14:38
 **/
public interface AliyunMessageService extends MessageService {

    /**
     * 发送短信
     * @param code 验证码
     * @param phoneNumbers 移动电话号码
     */
    void sendMessage(String code, String phoneNumbers);

    /**
     * 生成code发送短信验证码
     * @param userId 用户id
     * @param phone 手机号
     */
    @Override
    void sendCode(String userId, String phone);

}