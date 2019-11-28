package com.art1001.supply.wechat.message.dto.request;

import com.art1001.supply.wechat.message.template.MessageToken;
import com.art1001.supply.wechat.message.template.WeChatAppMessageTemplate;
import lombok.Data;
import lombok.ToString;

/**
 * @author heshaohua
 * @date 2019/11/7 14:33
 **/
@Data
@ToString
@SuppressWarnings("all")
public class PushRequestParam {

    /**
     * 接口调用验证token
     */
    private MessageToken access_token;

    /**
     * 用户在小程序的openid
     */
    private String touser;

    /**
     * 小程序模板消息相关的信息
     */
    private WeChatAppMessageTemplate weapp_template_msg;

    /**
     * 公众号模板消息相关的信息
     */
    private Object mp_template_msg;

    /**
     * token信息
     */
    private MessageToken tokenInfo;

    public static PushRequestParam newInstance(MessageToken accessToken, String toUser, WeChatAppMessageTemplate weAppTemplateMsg, Object mpTemplateMsg){
        return new PushRequestParam(accessToken, toUser, weAppTemplateMsg, mpTemplateMsg);
    }

    private PushRequestParam(MessageToken accessToken, String toUser, WeChatAppMessageTemplate weAppTemplateMsg, Object mpTemplateMsg){
         this.tokenInfo = accessToken;
         this.touser = toUser;
         this.weapp_template_msg = weAppTemplateMsg;
         this.mp_template_msg = mpTemplateMsg;
    }
}
