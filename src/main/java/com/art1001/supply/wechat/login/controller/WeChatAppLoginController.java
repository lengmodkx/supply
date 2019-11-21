package com.art1001.supply.wechat.login.controller;

import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.wechat.login.service.WeChatAppLogin;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.codec.Base64;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;

/**
 * @author heshaohua
 * @date 2019/11/20 13:50
 **/
@Slf4j
@RestController
@RequestMapping("/wx_app")
public class WeChatAppLoginController extends BaseController {

    @Resource
    private WeChatAppLogin weChatAppLogin;


    /**
     * 微信小程序登录
     */
    @GetMapping("/login")
    public Object wxAppLogin(@Validated @NotNull(message = "code 不能为空！") String code){
        log.info("weChat app login.[{}]", code);

        return success(weChatAppLogin.login(code));
    }


    /**
     * 解密并且获取用户手机号码
     * @param data 加密数据
     * @param iv 偏移量
     * @param key 用于解密
     */
    @RequestMapping(value = "deciphering", method = RequestMethod.GET)
    public String deciphering(String data, String iv, String key) {
        byte[] encrypData = Base64.decode(data);
        byte[] ivData = Base64.decode(iv);
        byte[] sessionKey = Base64.decode(key);
        String str="";
        try {
            str = decrypt(sessionKey,ivData,encrypData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(str);
        return str;
    }

    private static String decrypt(byte[] key, byte[] iv, byte[] encData) throws Exception {
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        //解析解密后的字符串  
        return new String(cipher.doFinal(encData), StandardCharsets.UTF_8);
}

}
