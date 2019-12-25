package com.art1001.supply.wechat.login.controller;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.util.RedisUtil;
import com.art1001.supply.wechat.login.dto.UpdateUserInfoRequest;
import com.art1001.supply.wechat.login.dto.WeChatDecryptResponse;
import com.art1001.supply.wechat.login.service.WeChatAppLogin;
import com.art1001.supply.wechat.message.template.MessageToken;
import com.art1001.supply.wechat.util.WeChatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Map;

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

    @Resource
    private UserService userService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private WeChatUtil weChatUtil;

    /**
     * 微信小程序登录
     */
    @GetMapping("/login")
    public Object wxAppLogin(@Validated @NotNull(message = "code 不能为空！") String code){
        log.info("weChat app login.[{}]", code);

        return success(weChatAppLogin.login(code));
    }

    /**
     * 存储微信小程序登录用户的信息
     * @param user 用户加密信息
     */
    @PostMapping("/user/info")
    public JSONObject updateWeChatUserInfo(@Validated UpdateUserInfoRequest user) throws Exception{
        log.info("save weChat user info. [{}]", user);

        JSONObject jsonObject = new JSONObject();

        WeChatDecryptResponse res = WeChatUtil.deciphering(
                user.getEncryptedData(), user.getIv(), redisUtil.get(Constants.WE_CHAT_SESSION_KEY_PRE + user.getOpenId()), WeChatDecryptResponse.class
        );

        redisUtil.remove(Constants.WE_CHAT_SESSION_KEY_PRE + user.getOpenId());

        UserEntity userEntity = userService.saveWeChatAppUserInfo(res);
        jsonObject.put("userInfo", userEntity);
        jsonObject.put("result", 1);
        return jsonObject;
    }

    /**
     * 绑定小程序手机号
     * @param data 加密数据
     * @param iv 偏移量
     */
    @PostMapping("/bind/phone")
    public Object getWeChatAppPhone(@Validated
                                    @RequestParam @NotNull(message = "data不能为空") String data,
                                    @Validated
                                    @RequestParam @NotNull(message = "加密偏移量不能为空")String iv,
                                    @Validated
                                    @RequestParam @NotNull(message = "code不能为空")String code
    ) throws Exception
     {
        log.info("Get wCchat login user phone. [{},{},{}]", data, iv, code);

        Map result = weChatAppLogin.bindPhone(data, iv, code);

        return success(result);
    }

    @GetMapping("access_token")
    public JSONObject getAccessToken(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",weChatUtil.getAccessToken());
        jsonObject.put("result", 1);
        return jsonObject;
    }
}
