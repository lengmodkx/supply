package com.art1001.supply.wechat.login.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author heshaohua
 * @date 2019/11/26 13:55
 **/
@Data
public class UpdateUserInfoRequest {

    @NotNull(message = "openId不能为空！")
    private String openId;

    private String iv;

    private String encryptedData;

    private String avatarUrl;

    private String city;

    private String country;

    private Integer gender;

    private String language;

    private String nickName;

    private String province;

    private String phone;

}