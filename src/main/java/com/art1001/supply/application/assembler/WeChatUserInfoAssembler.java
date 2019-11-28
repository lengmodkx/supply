package com.art1001.supply.application.assembler;

import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.util.ObjectsUtil;
import com.art1001.supply.wechat.login.dto.UpdateUserInfoRequest;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author heshaohua
 * @date 2019/11/26 13:59
 **/
@Component
public class WeChatUserInfoAssembler {

    public UserEntity weChatUserTransUserEntity(UpdateUserInfoRequest param){
        if(ObjectsUtil.isEmpty(param)){
            return null;
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(param.getNickName());
        userEntity.setAddress(param.getProvince());
        userEntity.setCreateTime(new Date());
        userEntity.setUpdateTime(new Date());
        userEntity.setImage(param.getAvatarUrl());
        userEntity.setDefaultImage(param.getAvatarUrl());
        userEntity.setSex(param.getGender());

        return userEntity;
    }
}
