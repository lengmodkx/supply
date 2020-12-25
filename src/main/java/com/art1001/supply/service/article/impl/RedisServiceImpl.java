package com.art1001.supply.service.article.impl;

import com.art1001.supply.service.article.RedisService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName RedisServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/12/25 13:40
 * @Discription
 */
@Service
public class RedisServiceImpl implements RedisService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void sendChannelMess(String channel, Object message) {

        stringRedisTemplate.convertAndSend(channel,message.toString());
    }
}
