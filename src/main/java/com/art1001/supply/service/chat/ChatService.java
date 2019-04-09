package com.art1001.supply.service.chat;

import com.art1001.supply.entity.chat.Chat;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * Service接口
 */
public interface ChatService extends IService<Chat> {

    List<Chat> findChatList(String projectId);

    /**
     * 根据id 查询出消息的详情信息 包括发送者信息
     * @param chatId 消息id
     * @return 消息信息
     */
    Chat findChatById(String chatId);
}