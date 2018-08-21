package com.art1001.supply.common;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.ServerMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author heshaohua
 * @Title: Push
 * @Description: 消息推送的封装类
 * @date 2018/8/21 14:00
 **/
@Component
public class Push {

    /**
     * 推送频道前缀
     */
    private static String pushHead = "/topic/";

    @Resource
    private SimpMessagingTemplate messagingTemplate;

    /** 用于订阅推送消息 */
    private static SimpMessagingTemplate messagePush;
    @PostConstruct
    public void init(){
        messagePush = messagingTemplate;
    }

    /**
     * 用于推送的方法
     * @param channel 频道
     * @param object 推送内容
     */
    public static void pushMessage(String channel, JSONObject object){
        messagePush.convertAndSend("/topic/" + channel,new ServerMessage(JSONObject.toJSONString(object)));
    }
}
