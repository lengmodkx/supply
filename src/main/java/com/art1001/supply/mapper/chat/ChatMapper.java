package com.art1001.supply.mapper.chat;

import com.art1001.supply.entity.chat.Chat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * mapper接口
 */
@Mapper
public interface ChatMapper extends BaseMapper<Chat> {
    List<Chat> findChatList();
}