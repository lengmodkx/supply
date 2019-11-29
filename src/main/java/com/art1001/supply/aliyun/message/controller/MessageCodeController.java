package com.art1001.supply.aliyun.message.controller;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.aliyun.message.service.MessageService;
import com.art1001.supply.aliyun.message.util.PhoneTest;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.RedisUtil;
import com.art1001.supply.wechat.message.configuration.WeChatAppMessageConfig;
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

    @Resource
    private RedisUtil redisUtil;

    private Long mm = 60L;

    private String flagPre = "aliyun:message:cooling:";

    private String frequencyPre = "aliyun:message:frequency";

    private Integer maxCount = 5;

    /**
     * 发送手机验证码
     * @param phone 手机号码
     * @return 结果
     */
    @RequestMapping("/code")
    public Object sendCode(
                            @NotNull(message = "用户id不能为空") String userId,
                            @NotNull(message = "手机号不能为空") String phone){

        if(redisUtil.exists(flagPre+userId)){
            return error("60秒内不能重复发短信。");
        }

        if(redisUtil.exists(frequencyPre + userId) && Integer.valueOf(redisUtil.get(frequencyPre + userId)) >= maxCount){
            return error("短信发送次数已经超出上限。");
        }

        PhoneTest.testPhone(phone);
        messageCodeService.sendCode(userId,phone);

        //设置短信冷却时间
        redisUtil.set(flagPre + userId, 1, mm);

        if(redisUtil.exists(frequencyPre + userId)){
            redisUtil.set(frequencyPre + userId, Integer.valueOf(redisUtil.get(frequencyPre + userId)) + 1);
        } else {
            redisUtil.set(frequencyPre + userId, 1);
        }

        return success();
    }
}
