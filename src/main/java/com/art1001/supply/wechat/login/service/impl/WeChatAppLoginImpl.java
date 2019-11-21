package com.art1001.supply.wechat.login.service.impl;

import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.util.JwtUtil;
import com.art1001.supply.util.ObjectsUtil;
import com.art1001.supply.wechat.login.dto.AppLoginResponse;
import com.art1001.supply.wechat.login.service.WeChatAppLogin;
import com.art1001.supply.wechat.util.WeChatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    @Value("${app-login-secret}")
    private String secret;


    @Override
    public Map<String, Object> login(String code) {
        Map<String,Object> resultMap = new HashMap<>(5);

        AppLoginResponse openIdAndSessionKey = weChatUtil.getOpenIdAndSessionKey(code);
        log.info("请求返回结果：[{}]" ,openIdAndSessionKey);

        if(ObjectsUtil.isNotEmpty(openIdAndSessionKey)){
            if(ObjectsUtil.isEmpty(openIdAndSessionKey.getErrcode())){
                log.info("小程序授权登录成功");
            } else {
                log.error("授权失败！[{},{}]", openIdAndSessionKey.getErrcode(), openIdAndSessionKey.getErrmsg());
                throw new ServiceException(openIdAndSessionKey.getErrmsg());
            }
        }

        //根据openid查询该用户是否登录过
        UserEntity byOpenId = userService.findById(openIdAndSessionKey.getOpenid());

        resultMap.put("userId", openIdAndSessionKey.getOpenid());

        UserEntity userEntity =  new UserEntity();
        userEntity.setUserId(openIdAndSessionKey.getOpenid());
        userEntity.setUserId(openIdAndSessionKey.getOpenid());
        userEntity.setUpdateTime(new Date());

        if(ObjectsUtil.isNotEmpty(byOpenId)){
            resultMap.put("updateInfo", false);
            resultMap.put("accessToken", JwtUtil.sign(byOpenId.getAccountName(), secret));
        } else {
            userEntity.setCreateTime(new Date());
            userEntity.setSessionKey(openIdAndSessionKey.getSession_key());
            userService.save(userEntity);
            resultMap.put("updateInfo", true);
        }
        return resultMap;
    }
}
