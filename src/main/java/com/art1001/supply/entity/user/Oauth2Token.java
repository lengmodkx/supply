package com.art1001.supply.entity.user;

import lombok.Data;

/**
 * @Description
 * @Date:2019/6/11 10:28
 * @Author heshaohua
 **/
@Data
public class Oauth2Token {


    // 网页授权接口调用凭证
     private String accessToken;
     // 凭证有效时长
     private int expiresIn;
     // 用于刷新凭证
     private String refreshToken;
     // 用户标识
     private String openId;
     // 用户授权作用域
     private String scope;
}
