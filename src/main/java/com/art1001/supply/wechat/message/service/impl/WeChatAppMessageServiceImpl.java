package com.art1001.supply.wechat.message.service.impl;

import com.art1001.supply.util.ObjectsUtil;
import com.art1001.supply.wechat.message.context.WeChatMessageContext;
import com.art1001.supply.wechat.message.dto.request.PushRequestParam;
import com.art1001.supply.wechat.message.dto.result.MessageResponseEntity;
import com.art1001.supply.wechat.message.service.WeChatAppMessageService;
import com.art1001.supply.wechat.message.template.WeChatAppMessageTemplate;
import com.art1001.supply.wechat.util.WeChatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author heshaohua
 * @date 2019/11/7 14:31
 **/
@Slf4j
@Service
public class WeChatAppMessageServiceImpl implements WeChatAppMessageService {

    @Resource
    private WeChatUtil weChatUtil;

    @Override
    public void pushToSingleUser(String appId, WeChatAppMessageTemplate weChatAppMessageTemplate) {
        PushRequestParam pushRequestParam = PushRequestParam.newInstance(
                weChatUtil.getAccessToken(), appId, weChatAppMessageTemplate, null
        );

        MessageResponseEntity res = weChatUtil.sendWeChatAppMessageRequest(pushRequestParam);
        if(ObjectsUtil.isNotEmpty(res)){
            log.error("错误码：[{}]，错误信息:[{}]", res.getErrcode(),res.getErrmsg());
        }
        log.info("小程序消息推送成功.[{},{}]", appId, res);


    }
}
