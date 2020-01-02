package com.art1001.supply.aliyun.message.controller;

import com.art1001.supply.aliyun.message.service.MessageService;
import com.art1001.supply.aliyun.message.util.PhoneTest;
import com.art1001.supply.api.base.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

/**
 * @author heshaohua
 * @date 2019/11/21 15:22
 **/
@Slf4j
@Validated
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
    public Object sendCode(
                            @NotNull(message = "用户id不能为空") String userId,
                            @NotNull(message = "手机号不能为空") String phone){

        PhoneTest.testPhone(phone);
        messageCodeService.sendCode(userId,phone);
        return success();
    }
}
