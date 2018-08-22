package com.art1001.supply.service.user.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.mapper.user.UserNewsMapper;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserNewsService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.user.UserNews;

/**
 * ServiceImpl
 */
@Service
public class UserNewsServiceImpl implements UserNewsService {

	/** Mapper接口*/
	@Resource
	private UserNewsMapper userNewsMapper;

	/** 任务逻辑层接口 */
	@Resource
	private TaskService taskService;

	/** 文件逻辑层接口 */
	@Resource
	private FileService fileService;

	/** 日程逻辑层接口 */
	@Resource
	private ScheduleService scheduleService;

	/** 分享逻辑层接口 */
	@Resource
	private ShareService shareService;

	@Resource
	private UserService userService;

	/** 用于订阅推送消息 */
	@Resource
	private SimpMessagingTemplate messagingTemplate;

	/**
	 * 查询分页数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<UserNews> findUserNewsPagerList(Pager pager){
		return userNewsMapper.findUserNewsPagerList(pager);
	}

	/**
	 * 通过id获取单条数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public UserNews findUserNewsById(String id){
		return userNewsMapper.findUserNewsById(id);
	}

	/**
	 * 通过id删除数据
	 * 
	 * @param id
	 */
	@Override
	public void deleteUserNewsById(String id){
		userNewsMapper.deleteUserNewsById(id);
	}

	/**
	 * 修改数据
	 * 
	 * @param userNews
	 */
	@Override
	public void updateUserNews(UserNews userNews){
		userNewsMapper.updateUserNews(userNews);
	}
	/**
	 * 保存数据
	 * 
	 * @param userNews
	 */
	@Override
	public void saveUserNews(UserNews userNews){
		userNewsMapper.saveUserNews(userNews);
	}
	/**
	 * 获取所有数据
	 * 
	 * @return
	 */
	@Override
	public List<UserNews> findUserNewsAllList(){
		return userNewsMapper.findUserNewsAllList();
	}

	/**
	 * 根据 信息id 和用户id  查询 用户有没有这消息的记录
	 * @param publicId 信息id
	 * @param userId 用户id
	 * @return
	 */
	@Override
	public int findUserNewsByPublicId(String publicId, String userId) {
		return userNewsMapper.findUserNewsByPublicId(publicId,userId);
	}

	/**
	 * 根据信息id 用户id 查询出 这条信息的消息数
	 * @param publicId 信息id
	 * @param userId 用户id
	 * @return 消息数
	 */
	@Override
	public Integer findNewsCountByPublicId(String publicId, String userId) {
		return userNewsMapper.findNewsCountByPublicId(publicId,userId);
	}

	/**
	 * 根据用户id  查询出用户的未读消息条数
	 * @param userId 用户id
	 * @return
	 */
	@Override
	public int findUserNewsCount(String userId) {
		return userNewsMapper.findUserNewsCount(userId);
	}

	/**
	 * 根据用户id 查询出该用户的 全部消息
	 * @param userId 用户id
	 * @return
	 */
	@Override
	public List<UserNews> findAllUserNewsByUserId(String userId) {
		return userNewsMapper.findAllUserNewsByUserId(userId);
	}

	/**
	 *	保存用户的消息信息
	 * @param users 受影响的用户数组
	 * @param publicId 哪条信息的消息
	 * @param publicType 信息的类型(任务,文件,日程,分享)
	 * @param content 消息内容
	 */
	@Override
	public void saveUserNews(String[] users, String publicId, String publicType, String content, int isChat) {

		String name = "";
		if(BindingConstants.BINDING_TASK_NAME.equals(publicType)){
			name = taskService.findTaskNameById(publicId);
		}

		if(BindingConstants.BINDING_FILE_NAME.equals(publicType)){
			name = fileService.findFileNameById(publicId);
		}

		if(BindingConstants.BINDING_SCHEDULE_NAME.equals(publicType)){
			name = scheduleService.findScheduleNameById(publicId);
		}

		if(BindingConstants.BINDING_SHARE_NAME.equals(publicType)){
			name = shareService.findShareNameById(publicId);

		}

		for(int i = 0;i < users.length;i++){
			if(isChat == 0){
				String uname = userService.findUserNameById(users[i]);
				content += " " + uname;
			}
			//如果本次循环的id  是当前操作的用户id 则跳过
			if(users[i].equals(ShiroAuthenticationManager.getUserId())){
				continue;
			}
			//查询该用户有没有该信息的 消息记录 如果没有添加一条 如果有在原来的消息数上 +1
			int result = userNewsMapper.findUserNewsByPublicId(publicId,users[i]);
			if(result == 0){
				UserNews userNews = new UserNews(IdGen.uuid(),name,content,publicId,0,ShiroAuthenticationManager.getUserId(),users[i],publicType,1,System.currentTimeMillis(),System.currentTimeMillis());
				userNewsMapper.saveUserNews(userNews);
			} else{
				UserNews userNews = new UserNews();
				userNews.setNewsContent(content);
				userNews.setNewsPublicId(publicId);
				userNews.setNewsHandle(0);
				userNews.setNewsToUserId(users[i]);
				userNews.setNewsCount(userNewsMapper.findNewsCountByPublicId(publicId,users[i])+1);
				userNews.setUpdateTime(System.currentTimeMillis());
				userNewsMapper.updateUserNews(userNews);
			}
			//查询出该用户的所有未读消息的总条数
			int newsCount = userNewsMapper.findUserNewsCount(users[i]);
			messagingTemplate.convertAndSend("/topic/"+ users[i],newsCount);
		}
	}

	/**
	 * 修改消息的 状态(已读,未读)  并且将消息条数设为0
	 * @param id 消息id
	 */
	@Override
	public void updateIsRead(String id) {
		userNewsMapper.updateIsRead(id);
	}

	/**
	 * 删除某个信息的所有通知消息
	 * @param publicId 信息id
	 */
	@Override
	public void deleteNewsByPublicId(String publicId) {
		userNewsMapper.deleteNewsByPublicId(publicId);
	}
}