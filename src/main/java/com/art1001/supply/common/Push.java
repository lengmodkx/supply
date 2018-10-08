package com.art1001.supply.common;

import com.alibaba.fastjson.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author heshaohua
 * @Title: Push
 * @Description: 消息推送的封装类
 * @date 2018/8/21 14:00
 **/
@Component
public class Push {

    /** 用于订阅推送消息 */
    @Resource
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 用于推送的方法
     * @param channel 频道
     * @param object 推送内容
     */
    public void pushMessage(String channel, JSONObject object){
        messagingTemplate.convertAndSend("/topic/" + channel,object);
    }
}
