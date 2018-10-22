package com.art1001.supply.service.notice.impl;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.notice.Notice;
import com.art1001.supply.service.notice.NoticeService;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import javax.annotation.Resource;

public class NoticeServiceImpl implements NoticeService {

    /** 用于订阅推送消息 */
    @Resource
    private SimpMessagingTemplate messagingTemplate;


    @Override
    public void pushMsg(String msgId,int type, JSONObject object) {
        Notice notice = new Notice();
        notice.setType(type);
        notice.setObject(object);
        messagingTemplate.convertAndSend("/topic/"+msgId,notice);
    }
}
