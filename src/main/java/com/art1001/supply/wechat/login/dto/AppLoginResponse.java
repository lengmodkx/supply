package com.art1001.supply.wechat.login.dto;

import lombok.Data;

/**
 * @author heshaohua
 * @date 2019/11/20 14:41
 **/
@Data
public class AppLoginResponse {

    private String openid;

    private String session_key;

    private String unionid;

    private Integer errcode;

    private String errmsg;
}
