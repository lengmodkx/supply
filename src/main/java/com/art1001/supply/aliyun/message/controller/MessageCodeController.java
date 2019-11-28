package com.art1001.supply.aliyun.message.controller;

import com.art1001.supply.aliyun.message.service.MessageService;
import com.art1001.supply.aliyun.message.util.PhoneTest;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.wechat.message.configuration.WeChatAppMessageConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author heshaohua
 * @date 2019/11/21 15:22
 **/
@Slf4j
@RestController
@RequestMapping("/message")
public class MessageCodeController extends BaseController {

    @Resource
    private MessageService messageCodeService;

    /**
     * 发送手机验证码
     * @param phone 手机号码
     * @return 结果
     */
    @RequestMapping("/code")
    public Object sendCode(String phone){
        log.info("send aliyun message.[{}]", phone);

        PhoneTest.testPhone(phone);
        messageCodeService.sendCode(phone);
        return success();
    }
}
