package com.art1001.supply.listener;

import com.alibaba.fastjson.JSON;
import com.art1001.supply.service.article.RedisService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName Redis2
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/12/25 16:56
 * @Discription
 */
@Slf4j
@Component
public class Redis2 {

    @Resource
    private CountDownLatch latch;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RedisService redisService;

    public Redis2(CountDownLatch latch) {
        this.latch = latch;
    }

    private  String msg = "";



    /**
     * 收到通道的消息之后执行的方法
     *
     * @param message
     */
    public void receiveMessage(Object message) {
        Map<String,String> map=(Map<String, String>) JSON.toJSON(message);
        String s = map.get(map.get(ShiroAuthenticationManager.getUserId()));
        getMsg1(s);
    }

   private String getMsg1(String s){
        msg=msg+s;
        return msg;
   }

   public String getMsg(){
       return msg;
   }

}
