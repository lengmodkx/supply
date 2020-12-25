package com.art1001.supply.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.service.article.RedisService;
import com.art1001.supply.util.RedisUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/***
 * 消息接收者（订阅者）  需要注入到springboot中
 */
@Slf4j
@Component
public class RedisReceiver {

    @Resource
    private CountDownLatch latch;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RedisService redisService;

    public RedisReceiver(CountDownLatch latch) {
        this.latch = latch;
    }


    /**
     * 收到通道的消息之后执行的方法
     *
     * @param message
     */
    public void receiveMessage(Object message) {
        JSONObject json = JSONObject.parseObject(message.toString());

        String memberId = (String) json.get("memberId");
        String articleId = (String) json.get("articleId");
        if (redisUtil.exists("feedMemberId:" + memberId)) {
            String s = redisUtil.get("feedMemberId:" + memberId);
            List<String> parse = (List<String>) JSON.parse(s);
            parse.stream().forEach(r->{
                Map<String,String> map= Maps.newHashMap();
                map.put(r,articleId);
                redisService.sendChannelMess(Constants.GET_MESSAGE_CHANNEL,map);
            });
        }

    }
}