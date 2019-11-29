package com.art1001.supply.aliyun.message.service.aliyun.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.art1001.supply.aliyun.message.context.aliyun.AliyunMessgaeContext;
import com.art1001.supply.aliyun.message.dto.MessageResponse;
import com.art1001.supply.aliyun.message.entity.CodeQueryParam;
import com.art1001.supply.aliyun.message.enums.KeyWord;
import com.art1001.supply.aliyun.message.exception.MessageSendException;
import com.art1001.supply.aliyun.message.service.aliyun.AliyunMessageService;
import com.art1001.supply.aliyun.message.util.CodeGen;
import com.art1001.supply.aliyun.message.util.PhoneTest;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.ObjectsUtil;
import com.art1001.supply.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author heshaohua
 * @date 2019/11/21 14:39
 **/
@Slf4j
@Service
public class AliyunMessageServiceImpl implements AliyunMessageService {

    @Resource
    private AliyunMessgaeContext messageContext;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public void sendMessage(String code, String phoneNumbers) {
        PhoneTest.testPhone(phoneNumbers);

        if(ObjectsUtil.isEmpty(code)){
            throw new MessageSendException("code码不能为空！");
        }

        IAcsClient client = new DefaultAcsClient(messageContext.buidDefaultProfile());

        CommonRequest commonRequest = messageContext.buidCommonRequest();

        commonRequest.putQueryParameter("PhoneNumbers", phoneNumbers);
        commonRequest.putQueryParameter("TemplateParam", JSONObject.toJSONString(CodeQueryParam.buildQueryParam(code)));
        try {
            CommonResponse response = client.getCommonResponse(commonRequest);
            MessageResponse messageResponse = JSONObject.parseObject(response.getData(), MessageResponse.class);

            log.info("短信发送返回参数：[{}]", messageResponse);
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void sendCode(String userId, String phone) {
        String code = CodeGen.getCode();
        this.sendMessage(code, phone);
        redisUtil.set(KeyWord.PREFIX.getCodePrefix() + userId, code,60*5L);
    }
}
