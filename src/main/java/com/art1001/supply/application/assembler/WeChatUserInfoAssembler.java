package com.art1001.supply.application.assembler;

import com.art1001.supply.aliyun.message.util.PhoneTest;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.BaseException;
import com.art1001.supply.util.IdGen;
import com.art1001.supply.util.ObjectsUtil;
import com.art1001.supply.wechat.login.dto.UpdateUserInfoRequest;
import com.art1001.supply.wechat.login.dto.WeChatDecryptResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author heshaohua
 * @date 2019/11/26 13:59
 **/
@Slf4j
@Component
public class WeChatUserInfoAssembler {

    public UserEntity weChatUserTransUserEntity(WeChatDecryptResponse param){
        if(ObjectsUtil.isEmpty(param)){
            return null;
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setAccountName(param.getPhone());
        userEntity.setUserId(IdGen.uuid());
        userEntity.setUserName(param.getNickName());
        userEntity.setAddress(param.getProvince());
        userEntity.setCreateTime(new Date());
        userEntity.setUpdateTime(new Date());
        userEntity.setImage(param.getAvatarUrl());
        userEntity.setDefaultImage(param.getAvatarUrl());
        userEntity.setSex(param.getGender());
        userEntity.setUpdateTime(new Date());
        userEntity.setCreateTime(new Date());
        userEntity.setWxUnionid(param.getUnionId());
        userEntity.setWxAppOpenid(param.getOpenId());
        userEntity.setCredentialsSalt(IdGen.uuid());
        return userEntity;
    }
}
