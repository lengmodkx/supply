package com.art1001.supply.service.notice;

public interface NoticeService {

    /**
     * 推送实体
     * @param msgId 消息订阅地址
     * @param type 消息类型
     * @param payload 消息内容
     */
    void pushMsg(String msgId,int type, Object payload);

}
