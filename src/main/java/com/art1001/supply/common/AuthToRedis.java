package com.art1001.supply.common;

import com.art1001.supply.service.resource.ProResourcesService;
import com.art1001.supply.util.RedisUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName AuthToRedis
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/8/4 17:31
 * @Discription
 */
@Component
public class AuthToRedis {

    @Resource
    private ProResourcesService proResourcesService;

    @Resource
    private RedisUtil redisUtil;

    public void setPermsAuth(String projectId,String userId){
        List<String> keyList = proResourcesService.getMemberResourceKey(projectId, userId);
        redisUtil.remove("perms:" + userId);
        redisUtil.lset("perms:" + userId, keyList);
    }


}
