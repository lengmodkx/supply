package com.art1001.supply.service.chat;

import com.art1001.supply.entity.chat.Chat;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * Service接口
 */
public interface ChatService extends IService<Chat> {

    List<Chat> findChatList(String projectId);
}