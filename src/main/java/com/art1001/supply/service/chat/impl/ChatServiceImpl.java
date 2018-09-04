package com.art1001.supply.service.chat.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.mapper.chat.ChatMapper;
import com.art1001.supply.service.chat.ChatService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.task.TaskService;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.chat.Chat;

/**
 * ServiceImpl
 */
@Service
public class ChatServiceImpl implements ChatService {

	/** Mapper接口*/
	@Resource
	private ChatMapper chatMapper;

	@Resource
	private FileService fileService;

	@Resource
	private ShareService shareService;

	@Resource
	private TaskService taskService;

	@Resource
	ScheduleService scheduleService;
	
	/**
	 * 查询分页数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Chat> findChatPagerList(Pager pager){
		return chatMapper.findChatPagerList(pager);
	}

	/**
	 * 通过chatId获取单条数据
	 * 
	 * @param chatId
	 * @return
	 */
	@Override 
	public Chat findChatByChatId(String chatId){
		return chatMapper.findChatByChatId(chatId);
	}

	/**
	 * 通过chatId删除数据
	 * 
	 * @param chatId
	 */
	@Override
	public void deleteChatByChatId(String chatId){
		chatMapper.deleteChatByChatId(chatId);
	}

	/**
	 * 修改数据
	 * 
	 * @param chat
	 */
	@Override
	public void updateChat(Chat chat){
		chatMapper.updateChat(chat);
	}
	/**
	 * 保存数据
	 * 
	 * @param chat
	 */
	@Override
	public void saveChat(Chat chat){
		chatMapper.saveChat(chat);
	}
	/**
	 * 获取所有数据
	 * 
	 * @return
	 */
	@Override
	public List<Chat> findChatAllList(){
		return chatMapper.findChatAllList();
	}

	/**
	 * 根据类型查询出 该项信息的参与者
	 * @param publicId 信息id
	 * @param publicType 信息类型
	 * @return 所有参与者的id 字符串
	 */
	@Override
	public String findMemberByPublicType(String publicId, String publicType) {
		String ids = "";
		if(BindingConstants.BINDING_FILE_NAME.equals(publicType)){
			ids = fileService.findUidsByFileId(publicId);
		}
		if(BindingConstants.BINDING_TASK_NAME.equals(publicType)){
			ids = taskService.findUidsByTaskId(publicId);
		}
		if(BindingConstants.BINDING_SCHEDULE_NAME.equals(publicType)){
			ids = scheduleService.findUidsByScheduleId(publicId);
		}
		if(BindingConstants.BINDING_SHARE_NAME.equals(publicType)){
			ids = shareService.findUidsByShareId(publicId);
		}
		return ids;
	}
}