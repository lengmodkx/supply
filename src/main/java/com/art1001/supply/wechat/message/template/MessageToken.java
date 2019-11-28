package com.art1001.supply.wechat.message.template;

import lombok.Data;

/**
 * @author heshaohua
 * @date 2019/11/25 16:52
 **/
@SuppressWarnings("all")
@Data
public class MessageToken {

    private String access_token;

    private String expires_in;

    private String errcode;

    private String errmsg;
}
