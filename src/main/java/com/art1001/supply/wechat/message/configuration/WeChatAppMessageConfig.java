package com.art1001.supply.wechat.message.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author heshaohua
 * @date 2019/11/7 14:54
 **/
@Data
@Component
public class WeChatAppMessageConfig {

    @Value("${app-appId}")
    private String appId;

    @Value("${app-secret}")
    private String secret;

    @Value("${app-message-grantType}")
    private String grantType;

    @Value("${app-message-token-root-url}")
    private String getAccessTokenRootUrl;

    @Value("${app-message-sendMessage-root-url}")
    private String sendMessageRootUrl;

}
