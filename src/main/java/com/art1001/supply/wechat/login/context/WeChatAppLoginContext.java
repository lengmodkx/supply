package com.art1001.supply.wechat.login.context;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author heshaohua
 * @date 2019/11/20 14:38
 **/
@Data
@Component
public class WeChatAppLoginContext {

    @Value("${app.appId}")
    private String appId;

    @Value("${app.secret}")
    private String secret;

    @Value("${app.login.grantType}")
    private String grantType;
}
