package com.art1001.supply.service.chat.impl;

import com.art1001.supply.entity.chat.Chat;
import com.art1001.supply.mapper.chat.ChatMapper;
import com.art1001.supply.service.chat.ChatService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
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
    public List<Chat> findChatList(String projectId) {
        List<Chat> chatList = chatMapper.findChatList(projectId);
        chatList.forEach(item -> {
            if(item.getMemberId().equals(ShiroAuthenticationManager.getUserId())){
                item.setIsOwn(1);
            } else{
                item.setIsOwn(0);
            }
        });
        return chatList;
    }

    /**
     * 根据id 查询出消息的详情信息 包括发送者信息
     * @param chatId 消息id
     * @return 消息信息
     */
    @Override
    public Chat findChatById(String chatId) {
        return chatMapper.findChatById(chatId);
    }
}