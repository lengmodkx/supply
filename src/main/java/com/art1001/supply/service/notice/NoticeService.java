package com.art1001.supply.service.notice;

public interface NoticeService {

    /**
     * 推送实体
     * @param msgId 消息订阅地址
     * @param type 消息类型
     * @param payload 消息内容
     */
    void pushMsg(String msgId,String type, Object payload);

    /**
     * 推送给指定的用户
     * @param userId 用户id
     * @param type 消息类型
     * @param payload 内容
     */
    void toUser(String userId,String type, Object payload);

    /**
     * 推送给指定的用户
     * @param userIds 多个用户id
     * @param type 消息类型
     * @param payload 内容
     */
    void toUsers(String[] userIds, String type, Object payload);

}
