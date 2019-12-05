package com.art1001.supply.wechat.message.service.impl;

import com.art1001.supply.util.ObjectsUtil;
import com.art1001.supply.wechat.message.dto.request.PushRequestParam;
import com.art1001.supply.wechat.message.dto.result.MessageResponseEntity;
import com.art1001.supply.wechat.message.exception.UserIdListIsEmptyException;
import com.art1001.supply.wechat.message.exception.WeChatDataBuildIsEmpty;
import com.art1001.supply.wechat.message.service.WeChatAppMessageService;
import com.art1001.supply.wechat.message.service.WeChatAppMessageTemplateDataBuildService;
import com.art1001.supply.wechat.message.template.WeChatAppMessageTemplate;
import com.art1001.supply.wechat.util.WeChatUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        if(ObjectsUtil.isNotEmpty(res.getErrcode())){
            log.error("错误码：[{}]，错误信息:[{}]", res.getErrcode(),res.getErrmsg());
        } else {
            log.info("小程序消息推送成功.[{},{}]", appId, res);
        }
    }

    @Override
    public void pushToMultipleUsers(List<String> userIdList,
                                    WeChatAppMessageTemplate weChatAppMessageTemplate,
                                    WeChatAppMessageTemplateDataBuildService weChatDataBuild
    ) {

        if(CollectionUtils.isEmpty(userIdList)){
            throw new UserIdListIsEmptyException("用户id列表不能为空！");
        }

        Optional.ofNullable(weChatDataBuild).orElseThrow(() -> new WeChatDataBuildIsEmpty("微信消息构造器不能为空！"));

        for (String userId : userIdList) {
            Map map = weChatDataBuild.buildData(userId);
            weChatAppMessageTemplate.setData(map);
            this.pushToSingleUser(userId, weChatAppMessageTemplate);
        }
    }
}
