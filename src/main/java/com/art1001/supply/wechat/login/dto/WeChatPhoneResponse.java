package com.art1001.supply.wechat.login.dto;

import lombok.Data;

/**
 * @author heshaohua
 * @date 2019/11/27 15:11
 **/
@Data
public class WeChatPhoneResponse {

    private String phoneNumber;

    private String purePhoneNumber;

    private String countryCode;
}
