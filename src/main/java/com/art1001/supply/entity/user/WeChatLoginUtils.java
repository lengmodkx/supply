package com.art1001.supply.entity.user;

import com.art1001.supply.util.IdGen;

/**
 * @Description
 * @Date:2019/6/3 12:46
 * @Author heshaohua
 **/
public class WeChatLoginUtils {

    public static String genUrl(){
        StringBuilder wechatUrl = new StringBuilder("https://open.weixin.qq.com/connect/qrconnect?appid=")
                .append(ConstansWeChat.APPID)
                .append("&redirect_uri")
                .append(ConstansWeChat.REDIRECT_URI)
                .append("&response_type=code")
                .append("&response_type=")
                .append(ConstansWeChat.SCOPE)
                .append("state=")
                .append(IdGen.uuid())
                .append("#wechat_redirect");
        return wechatUrl.toString();
    }
}
