package com.art1001.supply.wechat.login.service.impl;

import com.art1001.supply.aliyun.message.util.PhoneTest;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.service.user.WechatAppIdInfoService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.shiro.util.JwtUtil;
import com.art1001.supply.util.ObjectsUtil;
import com.art1001.supply.util.RedisUtil;
import com.art1001.supply.wechat.login.dto.AppLoginResponse;
import com.art1001.supply.wechat.login.dto.WeChatDecryptResponse;
import com.art1001.supply.wechat.login.dto.WeChatPhoneResponse;
import com.art1001.supply.wechat.login.entity.WechatAppIdInfo;
import com.art1001.supply.wechat.login.service.WeChatAppLogin;
import com.art1001.supply.wechat.util.WeChatUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author heshaohua
 * @date 2019/11/20 14:37
 **/
@Slf4j
@Service
public class WeChatAppLoginImpl implements WeChatAppLogin {

    @Resource
    private WeChatUtil weChatUtil;

    @Resource
    private UserService userService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private WechatAppIdInfoService wechatAppIdInfoService;


    @Value("${app.login.secret}")
    private String secret;


    @Override
    public Map<String, Object> login(String code) {
        Map<String,Object> resultMap = new HashMap<>(5);

        AppLoginResponse openIdAndSessionKey = weChatUtil.getOpenIdAndSessionKey(code);
        //根据授权返回信息中的openid查询该用户信息是否在数据库中存在
        LambdaQueryWrapper<WechatAppIdInfo> selectByOpenId = new QueryWrapper<WechatAppIdInfo>().lambda()
                .eq(WechatAppIdInfo::getOpenId, openIdAndSessionKey.getOpenid());

        WechatAppIdInfo wechatAppIdInfo = wechatAppIdInfoService.getOne(selectByOpenId);

        if(ObjectsUtil.isNotEmpty(wechatAppIdInfo)){
            UserEntity byId = userService.getById(wechatAppIdInfo.getUserId());
            resultMap.put("updateInfo", false);
            resultMap.put("getPhone", false);

            //验证手机号是否正确，如果不正确则需要通过小程序绑定手机号
            try {
                PhoneTest.testPhone(byId.getAccountName());
            } catch (Exception e){
                log.error("手机号码验证错误，需要重新绑定手机号。[{}]", e.getMessage());
                resultMap.put("getPhone", true);
            }
            resultMap.put("accessToken", JwtUtil.sign(byId.getAccountName(), byId.getCredentialsSalt()));

        } else {
            redisUtil.set(Constants.WE_CHAT_SESSION_KEY_PRE + openIdAndSessionKey.getOpenid(), openIdAndSessionKey.getSession_key());
            resultMap.put("updateInfo", true);
            resultMap.put("id", openIdAndSessionKey.getOpenid());
            return resultMap;
        }
        return resultMap;
    }

    @Override
    public Map bindPhone(String data, String iv, String code) throws Exception {
        //请求微信服务器授权
        AppLoginResponse openIdAndSessionKey = weChatUtil.getOpenIdAndSessionKey(code);

        WeChatPhoneResponse deciphering = WeChatUtil.deciphering(
                data, iv, openIdAndSessionKey.getSession_key(), WeChatPhoneResponse.class);
        //绑定手机号
        String userId = ShiroAuthenticationManager.getUserId();
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userId);
        userEntity.setAccountName(deciphering.getPhoneNumber());
        userEntity.setUpdateTime(new Date());
        userService.updateById(userEntity);

        userEntity = userService.getById(userId);

        Map<String,Object> resultMap = new HashMap<>(2);

        resultMap.put("userInfo",userEntity);
        resultMap.put("accessToken", JwtUtil.sign(userEntity.getAccountName(), userEntity.getCredentialsSalt()));
        return resultMap;

    }
}
