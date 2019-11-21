package com.art1001.supply.wechat.message.context;

import com.art1001.supply.util.ObjectsUtil;
import com.art1001.supply.wechat.util.WeChatUtil;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author heshaohua
 * @date 2019/11/7 14:32
 **/
@Data
@Component
public class WeChatMessageContext {

    private WeChatUtil weChatUtil = new WeChatUtil();
    private String accessToken;

    /**
     * 获取发送消息请求用的token
     * @return accessToken
     */
    public String getAccessToken() {
        if(ObjectsUtil.isNotEmpty(accessToken)){
            return accessToken;
        } else {
            return (accessToken = weChatUtil.getAccessToken());
        }
    }
}
