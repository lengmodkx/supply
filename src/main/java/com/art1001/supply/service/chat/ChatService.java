package com.art1001.supply.service.chat;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.chat.Chat;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * Service接口
 */
public interface ChatService extends IService<Chat> {

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
	 * @param chat 消息内容
	 * @param files 文件的内容 (json格式)
	 */
	public void saveChat(Chat chat,String files);

	/**
	 * 获取所有数据
	 * 
	 * @return
	 */
	public List<Chat> findChatAllList();

	/**
	 * 根据类型查询出 该项信息的参与者
	 * @param publicId 信息id
	 * @param publicType 信息类型
	 * @return 所有参与者的id 字符串
	 */
	String findMemberByPublicType(String publicId, String publicType);
	
}