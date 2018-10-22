package com.art1001.supply.service.notice;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

@Service
public interface NoticeService {

    /**
     * 发送消息
     * @param msgId 消息订阅地址
     * @param type 消息类型
     * @param object 消息内容
     */
    void pushMsg(String msgId,int type, JSONObject object);

}
