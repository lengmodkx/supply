package com.art1001.supply.mapper.chat;

import com.art1001.supply.entity.chat.Chat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * mapper接口
 */
@Mapper
public interface ChatMapper extends BaseMapper<Chat> {
    List<Chat> findChatList(@Param("projectId") String projectId);

    Chat findChatById(@Param("chatId") String chatId);
}