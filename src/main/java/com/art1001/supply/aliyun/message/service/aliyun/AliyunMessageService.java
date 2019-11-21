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


    void sendCode(String phone);

}