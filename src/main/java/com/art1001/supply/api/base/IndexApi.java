package com.art1001.supply.api.base;

import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.user.UserEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("index")
@RestController
public class IndexApi {

    @RequestMapping("test")
    public Result<UserEntity> testIndex(){
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName("wangayfeng");
        return Result.success(userEntity);
    }
}
