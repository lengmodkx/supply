package com.art1001.supply.entity.user;

import com.art1001.supply.util.IdGen;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @Description
 * @Date:2019/6/3 12:46
 * @Author heshaohua
 **/
public class WeChatLoginUtils {

    public static String genUrl(String redirectUri){
        try {
            String encode = URLEncoder.encode(redirectUri, "utf-8");
            StringBuilder wechatUrl = new StringBuilder("https://open.weixin.qq.com/connect/qrconnect?appid=")
                    .append(ConstansWeChat.APPID)
                    .append("&redirect_uri=")
                    .append(encode)
                    .append("&response_type=code")
                    .append("&scope=")
                    .append(ConstansWeChat.SCOPE)
                    .append("&state=")
                    .append(IdGen.uuid())
                    .append("#wechat_redirect");
            return wechatUrl.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
        //wxb7b91f87460a9d90
    }
}
