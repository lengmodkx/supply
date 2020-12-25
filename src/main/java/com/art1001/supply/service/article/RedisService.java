package com.art1001.supply.service.article;

public interface RedisService  {
    /**
     * 向通道发送消息
     * @param channel
     * @param message
     */
    void sendChannelMess(String channel, Object message);
}
