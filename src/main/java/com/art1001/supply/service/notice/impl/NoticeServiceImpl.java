package com.art1001.supply.service.notice.impl;

import com.art1001.supply.entity.notice.Notice;
import com.art1001.supply.service.notice.NoticeService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class NoticeServiceImpl implements NoticeService {

    /** 用于订阅推送消息 */
    @Resource
    private SimpMessagingTemplate messagingTemplate;


    @Override
    public void pushMsg(String msgId,int type, Object payload) {
        Notice notice = new Notice();
        notice.setType(type);
        notice.setObject(payload);
        messagingTemplate.convertAndSend("/topic/"+msgId,notice);
    }
}
