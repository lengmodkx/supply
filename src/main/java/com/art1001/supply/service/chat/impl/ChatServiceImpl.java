package com.art1001.supply.service.chat.impl;

import com.art1001.supply.entity.chat.Chat;
import com.art1001.supply.mapper.chat.ChatMapper;
import com.art1001.supply.service.chat.ChatService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jodd.util.ObjectUtil;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

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
            if(ObjectUtils.equals(ShiroAuthenticationManager.getUserId(),item.getMemberId())){
                item.setIsOwn(1);
            } else{
                item.setIsOwn(0);
            }
        });
        return chatList;
    }
}