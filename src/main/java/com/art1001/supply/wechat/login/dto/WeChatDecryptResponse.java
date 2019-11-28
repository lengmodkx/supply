package com.art1001.supply.wechat.login.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author heshaohua
 * @date 2019/11/26 16:26
 **/
@SuppressWarnings("all")
@Data
public class WeChatDecryptResponse extends UpdateUserInfoRequest{


    private String unionId;

    private String openId;
}
