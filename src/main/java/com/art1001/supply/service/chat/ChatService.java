package com.art1001.supply.service.chat;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.chat.Chat;

/**
 * Service接口
 */
public interface ChatService {

	/**
	 * 查询分页数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<Chat> findChatPagerList(Pager pager);

	/**
	 * 通过chatId获取单条数据
	 * 
	 * @param chatId
	 * @return
	 */
	public Chat findChatByChatId(String chatId);

	/**
	 * 通过chatId删除数据
	 * 
	 * @param chatId
	 */
	public void deleteChatByChatId(String chatId);

	/**
	 * 修改数据
	 * 
	 * @param chat
	 */
	public void updateChat(Chat chat);

	/**
	 * 保存数据
	 * 
	 * @param chat
	 */
	public void saveChat(Chat chat);

	/**
	 * 获取所有数据
	 * 
	 * @return
	 */
	public List<Chat> findChatAllList();
	
}