package com.art1001.supply.wechat.message.context;

import com.art1001.supply.util.ObjectsUtil;
import com.art1001.supply.wechat.message.template.MessageToken;
import com.art1001.supply.wechat.util.WeChatUtil;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author heshaohua
 * @date 2019/11/7 14:32
 **/
@Data
@Component
public class WeChatMessageContext {

    @Resource
    private WeChatUtil weChatUtil;

    private String accessToken;

}
