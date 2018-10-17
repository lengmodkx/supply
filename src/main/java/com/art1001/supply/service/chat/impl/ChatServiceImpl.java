package com.art1001.supply.service.chat.impl;

import com.art1001.supply.entity.chat.Chat;
import com.art1001.supply.mapper.chat.ChatMapper;
import com.art1001.supply.service.chat.ChatService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * ServiceImpl
 */
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements ChatService {

    @Resource
    private ChatMapper chatMapper;

    @Override
    public List<Chat> findChatList() {
        return chatMapper.findChatList();
    }
}