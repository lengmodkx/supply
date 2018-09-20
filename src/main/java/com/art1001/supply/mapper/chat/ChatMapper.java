package com.art1001.supply.mapper.chat;

import java.util.List;
import com.art1001.supply.entity.chat.Chat;
import com.art1001.supply.entity.base.Pager;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * mapper接口
 */
@Mapper
public interface ChatMapper extends BaseMapper<Chat> {

	/**
	 * 查询分页数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<Chat> findChatPagerList(Pager pager);

	/**
	 * 通过chatId获取单条数据
	 * 
	 * @param chatId
	 * @return
	 */
	Chat findChatByChatId(String chatId);

	/**
	 * 通过chatId删除数据
	 * 
	 * @param chatId
	 */
	void deleteChatByChatId(String chatId);

	/**
	 * 修改数据
	 * 
	 * @param chat
	 */
	void updateChat(Chat chat);

	/**
	 * 保存数据
	 * 
	 * @param chat
	 */
	void saveChat(Chat chat);

	/**
	 * 获取所有数据
	 * 
	 * @return
	 */
	List<Chat> findChatAllList();

}